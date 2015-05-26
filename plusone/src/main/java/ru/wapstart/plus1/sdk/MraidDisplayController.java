package ru.wapstart.plus1.sdk;

import java.util.ArrayList;

import ru.wapstart.plus1.sdk.MraidView.ExpansionStyle;
import ru.wapstart.plus1.sdk.MraidView.NativeCloseButtonStyle;
import ru.wapstart.plus1.sdk.MraidView.PlacementType;
import ru.wapstart.plus1.sdk.MraidView.ViewState;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class MraidDisplayController extends MraidAbstractController {
	private static final String LOGTAG = "MraidDisplayController";
	private static final long VIEWABILITY_TIMER_MILLIS = 3000;
	private static final int CLOSE_BUTTON_SIZE_DP = 50;

	private static final FrameLayout.LayoutParams CUSTOM_VIEW_LAYOUT_PARAMS =
		new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT,
			Gravity.CENTER
		);

	// The view's current state.
	private ViewState mViewState = ViewState.HIDDEN;

	// Tracks whether this controller's view responds to expand() calls.
	private final ExpansionStyle mExpansionStyle;

	// Tracks how this controller's view should display its native close button.
	private final NativeCloseButtonStyle mNativeCloseButtonStyle;

	// Separate instance of MraidView, for displaying "two-part" creatives via the expand(URL) API.
	private MraidView mTwoPartExpansionView;

	// A reference to the root view.
	private FrameLayout mRootView;

	// Container for custom views of WebView
	private FrameLayout mCustomViewContainer;

	// Tracks whether this controller's view is currently on-screen.
	private boolean mIsViewable;

	// Controls check viewability task and orientation broadcast logic
	private boolean mStartedTasks = false;

	// Task that periodically checks whether this controller's view is on-screen.
	private Runnable mCheckViewabilityTask = new Runnable() {
		public void run() {
			Log.d(LOGTAG, "mCheckViewabilityTask");
			boolean currentViewable = checkViewable();
			if (mIsViewable != currentViewable) {
				mIsViewable = currentViewable;
				getView().fireChangeEventForProperty(
					MraidViewableProperty.createWithViewable(mIsViewable));
			}
			mHandler.postDelayed(this, VIEWABILITY_TIMER_MILLIS);
		}
	};

	// Handler for scheduling viewability checks.
	private Handler mHandler = new Handler();

	// Stores the requested orientation for the Activity to which this controller's view belongs.
	// This is needed to restore the Activity's requested orientation in the event that the view
	// itself requires an orientation lock.
	private final int mOriginalRequestedOrientation;

	private BroadcastReceiver mOrientationBroadcastReceiver = new BroadcastReceiver() {
		private int mLastRotation;

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
				int orientation = MraidDisplayController.this.getDisplayRotation();
				if (orientation != mLastRotation) {
					mLastRotation = orientation;
					MraidDisplayController.this.onOrientationChanged(mLastRotation);
				}
			}
		}
	};

	// Native close button, used for expanded content.
	private ImageView mCloseButton;

	// Tracks whether expanded content provides its own, non-native close button.
	private boolean mAdWantsCustomCloseButton;

	// The scale factor for a dip (relative to a 160 dpi screen).
	protected float mDensity;

	// The width of the screen in pixels.
	protected int mScreenWidth = -1;

	// The height of the screen in pixels.
	protected int mScreenHeight = -1;

	// The view's position within its parent.
	private int mViewIndexInParent;

	MraidDisplayController(MraidView view, MraidView.ExpansionStyle expStyle,
			MraidView.NativeCloseButtonStyle buttonStyle) {
		super(view);
		mExpansionStyle = expStyle;
		mNativeCloseButtonStyle = buttonStyle;

		Context context = getView().getContext();
		mOriginalRequestedOrientation = (context instanceof Activity) ?
			((Activity) context).getRequestedOrientation() :
			ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

		initialize();
	}

	private void initialize() {
		mViewState = ViewState.LOADING;
		initializeScreenMetrics();
	}

	private void initializeScreenMetrics() {
		Context context = getView().getContext();
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;

		int statusBarHeight = 0, titleBarHeight = 0;
		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			Window window = activity.getWindow();
			Rect rect = new Rect();
			window.getDecorView().getWindowVisibleDisplayFrame(rect);
			statusBarHeight = rect.top;
			int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
			titleBarHeight = contentViewTop - statusBarHeight;
		}

		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels - statusBarHeight - titleBarHeight;
		mScreenWidth = (int) (widthPixels * (160.0 / metrics.densityDpi));
		mScreenHeight = (int) (heightPixels * (160.0 / metrics.densityDpi));
	}

	private int getDisplayRotation() {
		WindowManager wm = (WindowManager) getView().getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getOrientation();
	}

	private void onOrientationChanged(int currentRotation) {
		initializeScreenMetrics();
		getView().fireChangeEventForProperty(
			MraidScreenSizeProperty.createWithSize(mScreenWidth, mScreenHeight)
		);
	}

	public void destroy() {
		stopTasks();

		if (mViewState == ViewState.EXPANDED) {
			getView().post(new Runnable() {
				public void run() {
					RelativeLayout expansionLayout =
						(RelativeLayout) mRootView.findViewById(
							MraidView.MODAL_CONTAINER_LAYOUT_ID
						);

					mRootView.removeView(expansionLayout);

					if (getView().getOnCloseListener() != null)
						getView().getOnCloseListener().onClose(getView(), ViewState.HIDDEN);
				}
			});
		}
	}

	public void stopTasks() {
		if (mStartedTasks) {
			getView().getContext().unregisterReceiver(mOrientationBroadcastReceiver);
			Log.d(LOGTAG, "Orientation broadcast receiver was unregistered");

			mHandler.removeCallbacks(mCheckViewabilityTask);

			mStartedTasks = false;
		}
	}

	public void startTasks() {
		if (!mStartedTasks) {
			mHandler.post(mCheckViewabilityTask);

			getView().getContext().registerReceiver(mOrientationBroadcastReceiver,
				new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
			);
			Log.d(LOGTAG, "Orientation broadcast receiver is registered");

			mStartedTasks = true;
		}
	}

	public void showCustomView(View view) {
		if (mRootView == null || !isExpanded()) {
			Log.e(LOGTAG, "Only expand mode usage allowed");
			return;
		}

		RelativeLayout expansionLayout = (RelativeLayout) mRootView.findViewById(
			MraidView.MODAL_CONTAINER_LAYOUT_ID);

		expansionLayout.setVisibility(View.GONE);

		FrameLayout customViewContainer = getCustomViewContainer();

		customViewContainer.addView(view, CUSTOM_VIEW_LAYOUT_PARAMS);
		customViewContainer.setVisibility(View.VISIBLE);
		customViewContainer.bringToFront();
	}

	public void hideCustomView(View view) {
		if (mRootView == null || !isExpanded()) {
			Log.e(LOGTAG, "Only expand mode usage allowed");
			return;
		}

		FrameLayout customViewContainer = getCustomViewContainer();

		customViewContainer.setVisibility(View.GONE);
		customViewContainer.removeView(view);
		removeCustomViewContainer();

		RelativeLayout expansionLayout = (RelativeLayout) mRootView.findViewById(
			MraidView.MODAL_CONTAINER_LAYOUT_ID);

		expansionLayout.setVisibility(View.VISIBLE);
	}

	protected void initializeJavaScriptState() {
		ArrayList<MraidProperty> properties = new ArrayList<MraidProperty>();
		properties.add(MraidScreenSizeProperty.createWithSize(mScreenWidth, mScreenHeight));
		properties.add(MraidViewableProperty.createWithViewable(mIsViewable));
		getView().fireChangeEventForProperties(properties);

		mViewState = ViewState.DEFAULT;
		getView().fireChangeEventForProperty(MraidStateProperty.createWithViewState(mViewState));
	}

	protected boolean isExpanded() {
		return (mViewState == ViewState.EXPANDED);
	}

	protected void close() {
		if (mViewState == ViewState.EXPANDED) {
			resetViewToDefaultState();
			setOrientationLockEnabled(false);
			mViewState = ViewState.DEFAULT;
			getView().fireChangeEventForProperty(MraidStateProperty.createWithViewState(mViewState));
		} else if (mViewState == ViewState.DEFAULT) {
			getView().setVisibility(View.INVISIBLE);
			mViewState = ViewState.HIDDEN;
			getView().fireChangeEventForProperty(MraidStateProperty.createWithViewState(mViewState));
		}

		if (getView().getOnCloseListener() != null) {
			getView().getOnCloseListener().onClose(getView(), mViewState);
		}
	}

	private void resetViewToDefaultState() {
		FrameLayout adContainerLayout =
			(FrameLayout) mRootView.findViewById(MraidView.AD_CONTAINER_LAYOUT_ID);
		RelativeLayout expansionLayout = (RelativeLayout) mRootView.findViewById(
			MraidView.MODAL_CONTAINER_LAYOUT_ID);
		FrameLayout placeholderView = (FrameLayout) mRootView.findViewById(
			MraidView.PLACEHOLDER_VIEW_ID);

		setNativeCloseButtonEnabled(false);
		adContainerLayout.removeAllViewsInLayout();
		mRootView.removeView(expansionLayout);

		getView().requestLayout();

		ViewGroup parent = (ViewGroup) placeholderView.getParent();
		parent.addView(getView(), mViewIndexInParent);
		parent.removeView(placeholderView);
		parent.invalidate();
	}

	protected void expand(String url, int width, int height, boolean shouldUseCustomClose,
			boolean shouldLockOrientation) {
		if (
			mExpansionStyle == MraidView.ExpansionStyle.DISABLED
			|| mViewState == ViewState.EXPANDED
		)
			return;

		if (url != null && !URLUtil.isValidUrl(url)) {
			getView().fireErrorEvent("expand", "URL passed to expand() was invalid.");
			return;
		}

		// Obtain the root content view, since that's where we're going to insert the expanded
		// content. We must do this before swapping the MraidView with its place-holder;
		// otherwise, getRootView() will return the wrong view (or null).
		mRootView = (FrameLayout) getView().getRootView().findViewById(android.R.id.content);

		useCustomClose(shouldUseCustomClose);
		setOrientationLockEnabled(shouldLockOrientation);
		swapViewWithPlaceholderView();

		View expansionContentView = getView();
		if (url != null) {
			mTwoPartExpansionView = new MraidView(getView().getContext(), ExpansionStyle.DISABLED,
				NativeCloseButtonStyle.AD_CONTROLLED, PlacementType.INLINE);
			mTwoPartExpansionView.setOnCloseListener(new MraidView.OnCloseListener() {
				public void onClose(MraidView view, ViewState newViewState) {
					close();
				}
			});
			mTwoPartExpansionView.loadUrl(url);
			expansionContentView = mTwoPartExpansionView;
		}

		ViewGroup expansionViewContainer = createExpansionViewContainer(
			expansionContentView,
			(int) (width * mDensity),
			(int) (height * mDensity)
		);
		mRootView.addView(expansionViewContainer, new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));

		if (
			mNativeCloseButtonStyle == MraidView.NativeCloseButtonStyle.ALWAYS_VISIBLE
			|| (
				!mAdWantsCustomCloseButton
				&& mNativeCloseButtonStyle != MraidView.NativeCloseButtonStyle.ALWAYS_HIDDEN
			)
		) {
			setNativeCloseButtonEnabled(true);
		}

		mViewState = ViewState.EXPANDED;
		getView().fireChangeEventForProperty(MraidStateProperty.createWithViewState(mViewState));
		if (getView().getOnExpandListener() != null) getView().getOnExpandListener().onExpand(getView());
	}

	private void swapViewWithPlaceholderView() {
		ViewGroup parent = (ViewGroup) getView().getParent();
		if (parent == null) return;

		FrameLayout placeholderView = new FrameLayout(getView().getContext());
		placeholderView.setId(MraidView.PLACEHOLDER_VIEW_ID);

		int index;
		int count = parent.getChildCount();
		for (index = 0; index < count; index++) {
			if (parent.getChildAt(index) == getView()) break;
		}

		mViewIndexInParent = index;
		parent.addView(placeholderView, index,
				new ViewGroup.LayoutParams(getView().getWidth(), getView().getHeight()));
		parent.removeView(getView());
	}

	private ViewGroup createExpansionViewContainer(View expansionContentView, int expandWidth,
			int expandHeight) {
		int closeButtonSize = (int) (CLOSE_BUTTON_SIZE_DP * mDensity + 0.5f);
		if (expandWidth < closeButtonSize) expandWidth = closeButtonSize;
		if (expandHeight < closeButtonSize) expandHeight = closeButtonSize;

		RelativeLayout expansionLayout = new RelativeLayout(getView().getContext());
		expansionLayout.setId(MraidView.MODAL_CONTAINER_LAYOUT_ID);

		View dimmingView = new View(getView().getContext());
		dimmingView.setBackgroundColor(Color.TRANSPARENT);
		dimmingView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		expansionLayout.addView(dimmingView, new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));

		FrameLayout adContainerLayout = new FrameLayout(getView().getContext());
		adContainerLayout.setId(MraidView.AD_CONTAINER_LAYOUT_ID);

		adContainerLayout.addView(expansionContentView, new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(expandWidth, expandHeight);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		expansionLayout.addView(adContainerLayout, lp);

		return expansionLayout;
	}

	private void setOrientationLockEnabled(boolean enabled) {
		Context context = getView().getContext();
		Activity activity = null;
		try {
			activity = (Activity) context;
			int requestedOrientation = enabled ?
				activity.getResources().getConfiguration().orientation :
				mOriginalRequestedOrientation;
			activity.setRequestedOrientation(requestedOrientation);
		} catch (ClassCastException e) {
			Log.d(LOGTAG, "Unable to modify device orientation.");
		}
	}

	private FrameLayout getCustomViewContainer() {
		if (mCustomViewContainer == null) {
			mCustomViewContainer = new FrameLayout(getView().getContext());
			mCustomViewContainer.setVisibility(View.GONE);
			mCustomViewContainer.setBackgroundColor(Color.BLACK);
			mRootView.addView(
				mCustomViewContainer,
				new FrameLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT
				)
			);
		}

		return mCustomViewContainer;
	}

	private void removeCustomViewContainer() {
		if (mCustomViewContainer != null) {
			mRootView.removeView(mCustomViewContainer);
			mCustomViewContainer = null;
		}
	}

	protected void setNativeCloseButtonEnabled(boolean enabled) {
		if (mRootView == null) return;

		FrameLayout adContainerLayout =
			(FrameLayout) mRootView.findViewById(MraidView.AD_CONTAINER_LAYOUT_ID);

		if (enabled) {
			if (mCloseButton == null) {
				StateListDrawable states = new StateListDrawable();
				states.addState(new int[] {-android.R.attr.state_pressed},
					getView().getResources().getDrawable(R.drawable.close_button_normal));
				states.addState(new int[] {android.R.attr.state_pressed},
					getView().getResources().getDrawable(R.drawable.close_button_pressed));
				mCloseButton = new ImageButton(getView().getContext());
				mCloseButton.setImageDrawable(states);
				mCloseButton.setBackgroundDrawable(null);
				mCloseButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						MraidDisplayController.this.close();
					}
				});
			}

			int buttonSize = (int) (CLOSE_BUTTON_SIZE_DP * mDensity + 0.5f);
			FrameLayout.LayoutParams buttonLayout = new FrameLayout.LayoutParams(
				buttonSize, buttonSize, Gravity.RIGHT);
			adContainerLayout.addView(mCloseButton, buttonLayout);
		} else {
			adContainerLayout.removeView(mCloseButton);
		}

		MraidView view = getView();
		if (view.getOnCloseButtonStateChangeListener() != null) {
			view.getOnCloseButtonStateChangeListener().onCloseButtonStateChange(view, enabled);
		}
	}

	protected void useCustomClose(boolean shouldUseCustomCloseButton) {
		mAdWantsCustomCloseButton = shouldUseCustomCloseButton;

		MraidView view = getView();
		boolean enabled = !shouldUseCustomCloseButton;
		if (view.getOnCloseButtonStateChangeListener() != null) {
			view.getOnCloseButtonStateChangeListener().onCloseButtonStateChange(view, enabled);
		}
	}

	protected boolean checkViewable() {
		// TODO: Perform more sophisticated check for viewable.
		return true;
	}
}

package ru.wapstart.plus1.sdk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.view.LayoutInflater;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.VideoView;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class MraidView extends BaseAdView {
	private static final String LOGTAG = "MraidView";

	private View mCustomView;

	private MraidBrowserController mBrowserController;
	private MraidDisplayController mDisplayController;

	private WebViewClient mWebViewClient;
	private WebChromeClient mWebChromeClient;

	private boolean mHasFiredReadyEvent;
	private String mMraidPath;
	private final PlacementType mPlacementType;

	static class MraidListenerInfo {
		private OnExpandListener mOnExpandListener;
		private OnCloseListener mOnCloseListener;
		private OnReadyListener mOnReadyListener;
		private OnFailureListener mOnFailureListener;
		private OnCloseButtonStateChangeListener mOnCloseButtonListener;
		private OnOpenListener mOnOpenListener;
	}
	private MraidListenerInfo mListenerInfo;

	/**
	 * NOTE: WebView workaround
	 * @see http://code.google.com/p/android/issues/detail?id=7189
	 */
	private OnTouchListener mTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus())
						v.requestFocus();
					break;
			}

			return false;
		}
	};

	public static final int PLACEHOLDER_VIEW_ID = 100;
	public static final int MODAL_CONTAINER_LAYOUT_ID = 101;
	public static final int AD_CONTAINER_LAYOUT_ID = 102;

	public enum ViewState {
		LOADING,
		DEFAULT,
		EXPANDED,
		HIDDEN
	}

	enum ExpansionStyle {
		ENABLED,
		DISABLED
	}

	enum NativeCloseButtonStyle {
		ALWAYS_VISIBLE,
		ALWAYS_HIDDEN,
		AD_CONTROLLED
	}

	enum PlacementType {
		INLINE,
		INTERSTITIAL
	}

	public MraidView(Context context) {
		this(context, ExpansionStyle.ENABLED, NativeCloseButtonStyle.AD_CONTROLLED,
				PlacementType.INLINE);
	}

	MraidView(Context context, ExpansionStyle expStyle, NativeCloseButtonStyle buttonStyle,
			PlacementType placementType) {
		super(context);
		mPlacementType = placementType;
		initialize(expStyle, buttonStyle);
	}

	private void initialize(ExpansionStyle expStyle, NativeCloseButtonStyle buttonStyle) {
		setScrollContainer(false);
		setBackgroundColor(Color.TRANSPARENT);

		setVerticalScrollBarEnabled(false);
		setHorizontalScrollBarEnabled(false);

		getSettings().setJavaScriptEnabled(true);

		mBrowserController = new MraidBrowserController(this);
		mDisplayController = new MraidDisplayController(this, expStyle, buttonStyle);

		mWebViewClient = new MraidWebViewClient();
		setWebViewClient(mWebViewClient);

		mWebChromeClient = new MraidWebChromeClient();
		setWebChromeClient(mWebChromeClient);

		mListenerInfo = new MraidListenerInfo();
	}

	@Override
	public void destroy() {
		if (mDisplayController != null) {
			mDisplayController.destroy();
			mBrowserController = null;
			mDisplayController = null;
		}

		super.destroy();
	}

	public void loadHtmlData(String data) {
		data = completeHtml(data);

		data = data.replace("<head>", "<head><script src='" + getMraidPath() + "'></script>");

		loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
	}

	@Override
	public void loadUrl(String url) {
		if (url.startsWith("file:")) {
			if (url == getMraidPath()) {
				super.loadUrl(url);
			}

			return;
		}

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		StringBuffer out = new StringBuffer();

		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream is = entity.getContent();
				byte[] b = new byte[4096];
				for (int n; (n = is.read(b)) != -1;) {
					out.append(new String(b, 0, n));
				}
			}
		} catch (ClientProtocolException e) {
			notifyOnFailureListener();
			return;
		} catch (IOException e) {
			notifyOnFailureListener();
			return;
		}

		loadHtmlData(out.toString());
	}

	@Override
	public void pauseAdView() {
		setOnTouchListener(null);

		if (mDisplayController != null)
			mDisplayController.stopTasks();

		super.pauseAdView();
	}

	@Override
	public void resumeAdView() {
		if (mDisplayController != null)
			mDisplayController.startTasks();

		setOnTouchListener(mTouchListener);

		super.resumeAdView();
	}

	@Override
	public boolean canGoBack() {
		return
			mCustomView != null
			|| getDisplayController().isExpanded()
			|| super.canGoBack();
	}

	@Override
	public void goBack() {
		if (mCustomView != null)
			mWebChromeClient.onHideCustomView();
		else if (getDisplayController().isExpanded())
			getDisplayController().close();
		else
			super.goBack();
	}

	private void notifyOnFailureListener() {
		if (mListenerInfo.mOnFailureListener != null) {
			mListenerInfo.mOnFailureListener.onFailure(this);
		}
	}

	// Controllers /////////////////////////////////////////////////////////////////////////////////

	protected MraidBrowserController getBrowserController() {
		return mBrowserController;
	}

	protected MraidDisplayController getDisplayController() {
		return mDisplayController;
	}

	// Listeners ///////////////////////////////////////////////////////////////////////////////////

	public void setOnExpandListener(OnExpandListener listener) {
		mListenerInfo.mOnExpandListener = listener;
	}

	public OnExpandListener getOnExpandListener() {
		return mListenerInfo.mOnExpandListener;
	}

	public void setOnCloseListener(OnCloseListener listener) {
		mListenerInfo.mOnCloseListener = listener;
	}

	public OnCloseListener getOnCloseListener() {
		return mListenerInfo.mOnCloseListener;
	}

	public void setOnReadyListener(OnReadyListener listener) {
		mListenerInfo.mOnReadyListener = listener;
	}

	public OnReadyListener getOnReadyListener() {
		return mListenerInfo.mOnReadyListener;
	}

	public void setOnFailureListener(OnFailureListener listener) {
		mListenerInfo.mOnFailureListener = listener;
	}

	public OnFailureListener getOnFailureListener() {
		return mListenerInfo.mOnFailureListener;
	}

	public void setOnCloseButtonStateChange(OnCloseButtonStateChangeListener listener) {
		mListenerInfo.mOnCloseButtonListener = listener;
	}

	public OnCloseButtonStateChangeListener getOnCloseButtonStateChangeListener() {
		return mListenerInfo.mOnCloseButtonListener;
	}

	public void setOnOpenListener(OnOpenListener listener) {
		mListenerInfo.mOnOpenListener = listener;
	}

	public OnOpenListener getOnOpenListener() {
		return mListenerInfo.mOnOpenListener;
	}

	// JavaScript injection ////////////////////////////////////////////////////////////////////////

	protected void injectJavaScript(String js) {
		if (js != null) super.loadUrl("javascript:" + js);
	}

	protected void fireChangeEventForProperty(MraidProperty property) {
		String json = "{" + property.toString() + "}";
		injectJavaScript("window.mraidbridge.fireChangeEvent(" + json + ");");
		Log.d(LOGTAG, "Fire change: " + json);
	}

	protected void fireChangeEventForProperties(ArrayList<MraidProperty> properties) {
		String props = properties.toString();
		if (props.length() < 2) return;

		String json = "{" + props.substring(1, props.length() - 1) + "}";
		injectJavaScript("window.mraidbridge.fireChangeEvent(" + json + ");");
		Log.d(LOGTAG, "Fire changes: " + json);
	}

	protected void fireErrorEvent(String action, String message) {
		injectJavaScript("window.mraidbridge.fireErrorEvent('" + action + "', '" + message + "');");
	}

	protected void fireReadyEvent() {
		injectJavaScript("window.mraidbridge.fireReadyEvent();");
	}

	protected void fireNativeCommandCompleteEvent(String command) {
		injectJavaScript("window.mraidbridge.nativeCallComplete('" + command + "');");
	}

	private boolean tryCommand(URI uri) {
		String commandType = uri.getHost();
		List<NameValuePair> list = URLEncodedUtils.parse(uri, "UTF-8");
		Map<String, String> params = new HashMap<String, String>();
		for (NameValuePair pair : list) {
			params.put(pair.getName(), pair.getValue());
		}

		MraidCommand command = MraidCommandRegistry.createCommand(commandType, params, this);
		if (command == null) {
			fireNativeCommandCompleteEvent(commandType);
			return false;
		} else {
			command.execute();
			fireNativeCommandCompleteEvent(commandType);
			return true;
		}
	}

	/**
	 * Copies a file from res/raw to <destinationFilename> in the application file directory.
	 */
	private String copyRawResourceToFilesDir(int resourceId, String destinationFilename) {
		InputStream is = getContext().getResources().openRawResource(resourceId);

		String destinationPath = getContext().getFilesDir().getAbsolutePath() + File.separator +
				destinationFilename;
		File destinationFile = new File(destinationPath);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(destinationFile);
		} catch (FileNotFoundException e) {
			return "";
		}

		byte[] b = new byte[8192];
		try {
			for (int n; (n = is.read(b)) != -1;) {
				fos.write(b, 0, n);
			}
		} catch (IOException e) {
			return "";
		} finally {
			try { is.close(); fos.close(); } catch (IOException e) { }
		}

		return destinationPath;
	}

	private class MraidWebViewClient extends WebViewClient {
		@Override
		public void onReceivedError(WebView view, int errorCode, String description,
				String failingUrl) {
			Log.d(LOGTAG, "Error: " + description);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Uri uri = Uri.parse(url);
			String scheme = uri.getScheme();

			if (scheme.equals("mraid")) {
				Log.d(LOGTAG, "MRAID command: " + url);
				tryCommand(URI.create(url)); // java.net.URI, not android.net.Uri
				return true;
			}

			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			try {
				getContext().startActivity(i);
				return true;
			} catch (ActivityNotFoundException e) {
				return false;
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (!mHasFiredReadyEvent && mDisplayController != null) {
				mDisplayController.initializeJavaScriptState();
				fireChangeEventForProperty(
					MraidPlacementTypeProperty.createWithType(mPlacementType)
				);
				fireReadyEvent();
				if (getOnReadyListener() != null) getOnReadyListener().onReady(MraidView.this);
				mHasFiredReadyEvent = true;
			}
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			Log.d(LOGTAG, "Loaded resource: " + url);
		}
	}

	private class MraidWebChromeClient extends WebChromeClient {
		private WebChromeClient.CustomViewCallback mCustomViewCallback;

		private Bitmap mDefaultVideoPoster;
		private View mVideoProgressView;

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			/*((Activity)getContext()).getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
			);*/

			if (mCustomView != null) {
				callback.onCustomViewHidden();
			} else {
				mCustomView = view;
				mCustomViewCallback = callback;

				VideoView customVideoView =
					(
						mCustomView instanceof FrameLayout
						&& ((FrameLayout)mCustomView).getFocusedChild() instanceof VideoView
					)
						? (VideoView) ((FrameLayout)mCustomView).getFocusedChild()
						: null;

				getDisplayController().showCustomView(mCustomView);

				if (customVideoView != null) {
					customVideoView.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							mp.stop();
							Log.d(LOGTAG, "onCompletion() fired");
							onHideCustomView();
						}
					});
					customVideoView.setOnErrorListener(new OnErrorListener() {
						public boolean onError(MediaPlayer mp, int what, int extra) {
							Log.e(LOGTAG, "Error in custom video, type " + what);
							return true;
						}
					});
					//customVideoView.start();
				}
			}
		}

		@Override
		public void onHideCustomView() {
			if (mCustomView != null) {
				getDisplayController().hideCustomView(mCustomView);
				mCustomViewCallback.onCustomViewHidden();
				mCustomView = null;
			}

			/*((Activity)getContext()).getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
			);*/
		}

		@Override
		public Bitmap getDefaultVideoPoster() {
			if (mDefaultVideoPoster == null) {
				mDefaultVideoPoster = BitmapFactory.decodeResource(
					getResources(), R.drawable.default_video_poster);
			}

			return mDefaultVideoPoster;
		}

		@Override
		public View getVideoLoadingProgressView() {
			if (mVideoProgressView == null) {
				LayoutInflater inflater = LayoutInflater.from(getContext());
				mVideoProgressView =
					inflater.inflate(R.layout.video_loading_progress, null);
			}

			return mVideoProgressView;
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			Log.d(LOGTAG, message);
			return false;
		}
	}

	private String getMraidPath() {
		// Inject the MRAID JavaScript bridge.
		if (mMraidPath == null) {
			mMraidPath = "file://" + copyRawResourceToFilesDir(R.raw.mraid, "mraid.js");
		}

		return mMraidPath;
	}

	public interface OnExpandListener {
		public void onExpand(MraidView view);
	}

	public interface OnCloseListener {
		public void onClose(MraidView view, ViewState newViewState);
	}

	public interface OnReadyListener {
		public void onReady(MraidView view);
	}

	public interface OnFailureListener {
		public void onFailure(MraidView view);
	}

	public interface OnCloseButtonStateChangeListener {
		public void onCloseButtonStateChange(MraidView view, boolean enabled);
	}

	public interface OnOpenListener {
		public void onOpen(MraidView view);
	}
}

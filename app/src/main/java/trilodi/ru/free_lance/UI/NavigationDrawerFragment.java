package trilodi.ru.free_lance.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

import trilodi.ru.free_lance.Adapters.NavifationListAdapter;
import trilodi.ru.free_lance.Components.AvatarDrawable;
import trilodi.ru.free_lance.Config;
import trilodi.ru.free_lance.Network.NetManager;
import trilodi.ru.free_lanceru.R;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private RelativeLayout mainDrawerLayout;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;


    Picasso mPicasso;
    ImageView avatar, proImage, verImage;
    TextView userNames, userEmail, reviewsCount, filterText, exitText;

    private com.squareup.picasso.Target loadtarget;

    public NavigationDrawerFragment() {
    }

    AlertDialog dialog;
    View loadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainDrawerLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        avatar = (ImageView) mainDrawerLayout.findViewById(R.id.avatarImage);
        proImage = (ImageView) mainDrawerLayout.findViewById(R.id.proImage);
        verImage = (ImageView) mainDrawerLayout.findViewById(R.id.verImage);

        userNames = (TextView) mainDrawerLayout.findViewById(R.id.userNames);
        userEmail = (TextView) mainDrawerLayout.findViewById(R.id.userEmail);
        reviewsCount = (TextView) mainDrawerLayout.findViewById(R.id.reviewsCount);
        TextView ratingText = (TextView) mainDrawerLayout.findViewById(R.id.ratingText);

        filterText = (TextView) mainDrawerLayout.findViewById(R.id.textView22);
        exitText = (TextView) mainDrawerLayout.findViewById(R.id.textView23);

        filterText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_filter, 0, 0, 0);
        exitText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_logout, 0, 0, 0);

        exitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingView = getActivity().getLayoutInflater().inflate(R.layout.load_dialog_layout, null);
                ProgressBarCircularIndeterminate progresser = (ProgressBarCircularIndeterminate) loadingView.findViewById(R.id.dialogProgress);

                TextView dialogTitle = (TextView) loadingView.findViewById(R.id.dialogtitle);
                TextView dialogDescription = (TextView) loadingView.findViewById(R.id.dialogDescription);

                dialogTitle.setText("Пожалуйста подождите...");
                dialogDescription.setText("Идет выход из приложения!");

                dialog = new AlertDialog.Builder(getActivity()).setView(loadingView).setCancelable(false).create();
                dialog.show();
                String delete = "DELETE FROM message";
                Config.db.rawQuery(delete, null);
                delete = "DELETE FROM user";
                Config.db.rawQuery(delete, null);
                delete = "DELETE FROM category";
                Config.db.rawQuery(delete, null);
                delete = "DELETE FROM category_group";
                Config.db.rawQuery(delete, null);
                delete = "DELETE FROM city";
                Config.db.rawQuery(delete, null);
                delete = "DELETE FROM country";
                Config.db.rawQuery(delete, null);
                delete = "DELETE FROM filter";
                Config.db.rawQuery(delete, null);
                delete = "DELETE FROM filter_item";
                Config.db.rawQuery(delete, null);

                SharedPreferences.Editor localEditor2 = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                localEditor2.putString("login", "");
                localEditor2.putString("password", "");
                localEditor2.putLong("login_time", 0);
                localEditor2.putString("id", "");
                localEditor2.putBoolean("first_launch_not_login", true);
                localEditor2.commit();

                RequestParams localRequestParams = new RequestParams();
                localRequestParams.put("method", "users_signout");
                NetManager.getInstance(getActivity()).post(localRequestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String str = new String(responseBody, "UTF-8");
                            System.out.println(str);
                            Intent splash = new Intent(getActivity(),SplashScreenActivity.class);
                            splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(splash);
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // new MessagesDialog(ProjectActivity.this, "Проект", "Во время загрузки проекта произошла ошибка соединения.\nПроверьте соединение и повторите попытку.").show();
                    }
                });
            }
        });

        filterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filter = new Intent(getActivity(), FilterActivity.class);
                filter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(filter);
            }
        });

        try{
            ratingText.setText("Рейтинг: "+Config.myUser.rating);
        }catch(Exception e){
            ratingText.setText("");
        }


        userNames.setText(Config.myUser.firstname);
        if(!Config.myUser.lastname.equals("")){
            userNames.setText(userNames.getText()+" "+Config.myUser.lastname);
        }
        if(!Config.myUser.username.equals("")){
            userNames.setText(userNames.getText()+" ["+Config.myUser.username+"]");
        }

        if(Config.myUser.pro==1){
            proImage.setVisibility(View.VISIBLE);
        }else{
            proImage.setVisibility(View.GONE);
        }

        if(Config.myUser.verified==1){
            verImage.setVisibility(View.VISIBLE);
        }else{
            verImage.setVisibility(View.GONE);
        }

        userEmail.setText(Config.myUser.email);

        reviewsCount.setText("Отзывов: "+Config.myUser.reviews.size());

        mPicasso = Picasso.with(avatar.getContext());

        if (loadtarget == null) {
            loadtarget = new com.squareup.picasso.Target()  {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap
                    try{
                        avatar.setImageBitmap(roundImage(bitmap));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable){

                }
            };
        }

        AvatarDrawable avatarDrawable = null;
        avatarDrawable = new AvatarDrawable(Config.myUser);
        avatar.setImageDrawable(avatarDrawable);

        if(!Config.myUser.avatar.get("file").equals("")){
            mPicasso.load(Config.myUser.avatar.get("url")+"f_"+Config.myUser.avatar.get("file")).into(loadtarget);
        }

        mDrawerListView = (ListView) mainDrawerLayout.findViewById(R.id.navList);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        NavifationListAdapter nla = new NavifationListAdapter(getActivity(),new String[]{
                getString(R.string.PROJECTS_TITLE),
                getString(R.string.MESSAGES_TITLE),
                getString(R.string.FAVORITES_TITLE),
                getString(R.string.PROFILE_TITLE),
        });

        mDrawerListView.setAdapter(nla);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mainDrawerLayout;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public Bitmap roundImage(Bitmap bm){
        //java.io.File image = new java.io.File(path);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        Bitmap bitmap = bm;

        Bitmap bitmapRounded = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(bitmapRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight())), 80, 80, paint);

        return bitmapRounded;
    }
}

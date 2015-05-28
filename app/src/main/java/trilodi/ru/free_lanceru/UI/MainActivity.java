package trilodi.ru.free_lanceru.UI;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import com.appodeal.ads.Appodeal;

import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.R;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static DrawerLayout drawerLayout;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;


    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        Appodeal.initialize(this, Config.appKey);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position){
            case 0:
                fragmentManager.beginTransaction().replace(R.id.container, ProjectsListFragment.newInstance()).commit();
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.container, MessagesFragmant.newInstance()).commit();
                break;
            case 2:
                fragmentManager.beginTransaction().replace(R.id.container, FavoritesFragment.newInstance()).commit();
                break;
            case 3:
                fragmentManager.beginTransaction().replace(R.id.container, ProfileFragment.newInstance()).commit();
                break;
            default:
                fragmentManager.beginTransaction().replace(R.id.container, ProjectsListFragment.newInstance()).commit();
        }


        //fragmentManager.beginTransaction()
              //  .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
             //   .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }






}

package trilodi.ru.free_lance.UI;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.appodeal.ads.Appodeal;
import com.google.android.gms.analytics.HitBuilders;

import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lanceru.R;

public class PortfolioActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        FreeLanceApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open").setLabel("Портфолио").build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_portfolio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        Appodeal.hide(this, Appodeal.BANNER_VIEW);
        //Appodeal.setBannerViewId(R.id.appodealBannerView);
        //Appodeal.show(this, Appodeal.BANNER_VIEW);
    }
}

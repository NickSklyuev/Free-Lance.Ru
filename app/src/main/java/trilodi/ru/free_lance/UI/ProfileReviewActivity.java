package trilodi.ru.free_lance.UI;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import com.appodeal.ads.Appodeal;
import com.google.android.gms.analytics.HitBuilders;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lanceru.R;

public class ProfileReviewActivity extends ActionBarActivity {


    ViewPager viewPager;
    SmartTabLayout viewPagerTab;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_review);

        FreeLanceApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open").setLabel("Отзывы о пользователе").build());

        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Положитеные", ReviewShowFragment.class, new Bundler().putString("rate", "1").get())
                .add("Отрицательные", ReviewShowFragment.class, new Bundler().putString("rate", "-1").get())
                .add("Нейтральные", ReviewShowFragment.class, new Bundler().putString("rate", "0").get())
                .create());

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);


    }

    @Override
    public void onResume(){
        super.onResume();
        Appodeal.hide(this, Appodeal.BANNER_VIEW);
        //Appodeal.setBannerViewId(R.id.appodealBannerView);
        //Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

}

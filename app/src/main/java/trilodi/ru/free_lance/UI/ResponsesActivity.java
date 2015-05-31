package trilodi.ru.free_lance.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lance.Models.Responses;
import trilodi.ru.free_lance.Adapters.HidingScrollListener;
import trilodi.ru.free_lance.Adapters.ResponsesListAdapter;
import trilodi.ru.free_lance.Components.BusProvider;
import trilodi.ru.free_lance.Components.DBOpenHelper;
import trilodi.ru.free_lance.Components.UpdateResponsesEvent;
import trilodi.ru.free_lance.Config;
import trilodi.ru.free_lanceru.R;

public class ResponsesActivity extends ActionBarActivity {

    private RecyclerView responsesRecyclerView;
    private ResponsesListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ArrayList<Responses> responses = new ArrayList<Responses>();

    private ImageView backButton;

    Toolbar tb;

    @Subscribe
    public void onUpdateResponses(UpdateResponsesEvent event){
        responses = event.responses;
    }

    private void hideViews() {
        tb.animate().translationY(-tb.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    private void showViews() {
        tb.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);

        FreeLanceApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open").setLabel("Ответы на проект").build());

        Config.dbHelper=new DBOpenHelper(this);
        Config.db = Config.dbHelper.getWritableDatabase();

        tb = (Toolbar) findViewById(R.id.login_toolbar);


        BusProvider.getInstance().register(this);

        backButton  = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        com.gc.materialdesign.views.ButtonFloat writeResponse = (com.gc.materialdesign.views.ButtonFloat) findViewById(R.id.buttonFloat);

        writeResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addResponse = new Intent(ResponsesActivity.this,AddResponse.class);
                addResponse.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(addResponse);
            }
        });

        responsesRecyclerView = (RecyclerView) findViewById(R.id.responsesList);
        responsesRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        responsesRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ResponsesListAdapter(responses);
        responsesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        responsesRecyclerView.setAdapter(mAdapter);

        responsesRecyclerView.setOnScrollListener(new HidingScrollListener(this) {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });

    }


}

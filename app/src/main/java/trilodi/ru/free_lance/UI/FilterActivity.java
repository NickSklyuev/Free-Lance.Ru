package trilodi.ru.free_lance.UI;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.google.android.gms.analytics.HitBuilders;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;

import java.util.ArrayList;

import trilodi.ru.free_lance.Adapters.FilterListAdapter;
import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lance.Adapters.CatGroupAdapter;
import trilodi.ru.free_lance.Adapters.CategoriesAdapter;
import trilodi.ru.free_lance.Components.BusProvider;
import trilodi.ru.free_lance.Components.DBOpenHelper;
import trilodi.ru.free_lance.Components.UpdateFilterEvent;
import trilodi.ru.free_lance.Components.UpdateProjectEvent;
import trilodi.ru.free_lance.Config;
import trilodi.ru.free_lance.Models.CatGroup;
import trilodi.ru.free_lance.Models.Categories;
import trilodi.ru.free_lance.Network.NetManager;
import trilodi.ru.free_lanceru.R;

public class FilterActivity extends ActionBarActivity {

    Switch filter_enabler;

    LinearLayout user_filter;

    boolean filter_enable=false;

    EditText keywords;

    String filterID="0";

    //TextView mText;
    ListView categoriesList;

    private RecyclerView filterRecyclerView;
    private FilterListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList catGIDs=new ArrayList();
    ArrayList catIDs=new ArrayList();
    public static ArrayList<Categories> categoriesMainList = new ArrayList<Categories>();
    ArrayList<CatGroup> catGroups = new ArrayList<CatGroup>();

    ArrayList<Categories> categories = new ArrayList<Categories>();

    ImageView okButton, backButton;

    Button add;

    int pos=0;

    @Subscribe
    public void onUpdateProject(UpdateFilterEvent event){
        System.out.println(event.cid);
        categoriesMainList.remove((int)event.cid);
        mAdapter = new FilterListAdapter(categoriesMainList);
        filterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        filterRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        FreeLanceApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open").setLabel("Фильтр проектов").build());

        BusProvider.getInstance().register(this);

        okButton = (ImageView) findViewById(R.id.OK);
        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add=(Button)findViewById(R.id.sendButton);

        catGIDs=new ArrayList();
        catIDs=new ArrayList();
        categoriesMainList = new ArrayList<Categories>();

        Config.dbHelper=new DBOpenHelper(this);
        Config.db = Config.dbHelper.getWritableDatabase();

        filter_enabler=(Switch)findViewById(R.id.switch2);
        user_filter=(LinearLayout)findViewById(R.id.user_filter);

        keywords=(EditText)findViewById(R.id.editText3);

        Cursor c = Config.db.query("filter", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int id = c.getColumnIndex("id");
            int enabler = c.getColumnIndex("enabled");
            int keyword = c.getColumnIndex("keyword");

            filterID=c.getString(id);

            if(c.getString(enabler).equals("1")){
                filter_enabler.setChecked(true);
                user_filter.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                filter_enable=true;
            }else{
                filter_enabler.setChecked(false);
                user_filter.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                filter_enable=false;
            }

            keywords.setText(c.getString(keyword));
        }
        c.close();

        c = Config.db.query("filter_item", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int cgi = c.getColumnIndex("category_group_id");
            int ci = c.getColumnIndex("category_id");

            do{
                catGIDs.add(c.getString(cgi));
                catIDs.add(c.getString(ci));
            }while (c.moveToNext());

        }
        c.close();

        for(int i=0; i<catGIDs.size();i++){
            c = Config.db.query("category", null, "id=? AND category_group_id=?", new String[]{catIDs.get(i).toString(),catGIDs.get(i).toString()}, null, null, null);
            if (c.moveToFirst()) {
                int id = c.getColumnIndex("id");
                int title = c.getColumnIndex("title");

                categoriesMainList.add(new Categories(c.getString(id),c.getString(title)));

            }
            c.close();
        }

        filterRecyclerView = (RecyclerView) findViewById(R.id.responsesList);
        filterRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        filterRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FilterListAdapter(categoriesMainList);
        filterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        filterRecyclerView.setAdapter(mAdapter);

        filter_enabler.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    filter_enabler.setChecked(true);
                    user_filter.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    filter_enable=true;
                }else{
                    filter_enabler.setChecked(false);
                    user_filter.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                    filter_enable=false;
                }

            }
        });

        catGroups = new ArrayList<CatGroup>();

        c = Config.db.query("category_group", null, null, null, null, null, "title ASC");
        if (c.moveToFirst()) {
            int catId = c.getColumnIndex("id");
            int catTitle = c.getColumnIndex("title");
            do{
                System.out.println(c.getString(catTitle));
                catGroups.add(new CatGroup(c.getString(catId),c.getString(catTitle)));
            }while (c.moveToNext());
        }
        c.close();


        View cgdView = getLayoutInflater().inflate(R.layout.dialog_cat_groups_layout, null);
        final AlertDialog catGroupDialog = new AlertDialog.Builder(this).setView(cgdView).setCancelable(true).create();
        TextView cgdTitle = (TextView) cgdView.findViewById(R.id.dialogtitle);
        cgdTitle.setText("Группы категорий");
        //final Dialog catGroupDialog = new Dialog(this);
        //catGroupDialog.setContentView(R.layout.dialog_cat_groups_layout);
        //catGroupDialog.setTitle("Группы категорий");
        //catGroupDialog.setCancelable(true);

        CatGroupAdapter cga=new CatGroupAdapter(catGroupDialog.getContext(),catGroups);
        ListView catGroupList = (ListView)cgdView.findViewById(R.id.listView7);
        catGroupList.setAdapter(cga);




        View cdView = getLayoutInflater().inflate(R.layout.dialog_cat_groups_layout, null);


        final AlertDialog categoriesDialog = new AlertDialog.Builder(this).setView(cdView).setCancelable(true).create();


        TextView cdTitle = (TextView) cdView.findViewById(R.id.dialogtitle);
        cdTitle.setText("Категории");





        categoriesList = (ListView)cdView.findViewById(R.id.listView7);



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catGroupDialog.show();
            }
        });


        categoriesDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                catGroupDialog.show();
            }
        });

        catGroupList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categories = new ArrayList<Categories>();
                catGroupDialog.dismiss();
                Cursor c = Config.db.query("category", null, "category_group_id=" + catGroups.get(position).getCatId(), null, null, null, "title ASC");
                if (c.moveToFirst()) {
                    int catId = c.getColumnIndex("id");
                    int catTitle = c.getColumnIndex("title");
                    do {
                        System.out.println(c.getString(catTitle));
                        categories.add(new Categories(c.getString(catId), c.getString(catTitle)));
                    } while (c.moveToNext());
                }
                c.close();

                CategoriesAdapter ca = new CategoriesAdapter(categoriesDialog.getContext(), categories);
                categoriesList.setAdapter(ca);

                categoriesDialog.show();
            }
        });



        categoriesList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println(categories.get(position).getCatTitle());

                categoriesMainList.add(new Categories(categories.get(position).getCatId(), categories.get(position).getCatTitle()));
                //FilterListAdapter ca=new FilterListAdapter(FilterActivity.this,categoriesMainList);
                //mainList.setAdapter(ca);
                mAdapter = new FilterListAdapter(categoriesMainList);
                filterRecyclerView.setItemAnimator(new DefaultItemAnimator());
                filterRecyclerView.setAdapter(mAdapter);

                categoriesDialog.dismiss();
            }
        });


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.db.delete("filter", null, null);
                Config.db.delete("filter_item", null, null);

                ContentValues cv = new ContentValues();
                cv.put("id", "1");
                if (filter_enable) {
                    cv.put("enabled", "1");
                } else {
                    cv.put("enabled", "0");
                }
                cv.put("keyword", keywords.getText().toString());
                Config.db.insert("filter", null, cv);

                catGIDs.clear();
                catIDs.clear();
                for (int i = 0; i < categoriesMainList.size(); i++) {
                    Cursor c = Config.db.query("category", null, "id=?", new String[]{categoriesMainList.get(i).getCatId()}, null, null, null);
                    if (c.moveToFirst()) {
                        int id = c.getColumnIndex("category_group_id");
                        catGIDs.add(c.getString(id));
                        catIDs.add(categoriesMainList.get(i).getCatId());
                        cv = new ContentValues();
                        cv.put("filter_id", "1");
                        cv.put("category_group_id", c.getString(id));
                        cv.put("category_id", categoriesMainList.get(i).getCatId());
                        Config.db.insert("filter_item", null, cv);
                    }
                    c.close();
                }

                //ProjectsFragment.onRefresh=true;

                RequestParams localRequestParams = new RequestParams();
                localRequestParams.put("method", "settings_filter_set");

                if (filter_enable) {
                    localRequestParams.put("enabled", "1");
                } else {
                    localRequestParams.put("enabled", "0");
                }
                localRequestParams.put("keyword", keywords.getText().toString());
                for (int i = 0; i < catGIDs.size(); i++) {
                    String str1 = String.format("items[%s][categories_group_id]", String.valueOf(i));
                    String str2 = String.format("items[%s][categories_id]", String.valueOf(i));

                    localRequestParams.put(str1, catGIDs.get(i).toString());
                    localRequestParams.put(str2, catIDs.get(i).toString());
                }
                NetManager.getInstance(FilterActivity.this).post(localRequestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        try {
                            String str = new String(responseBody, "UTF-8");
                            System.out.println(str);

                            BusProvider.getInstance().post(new UpdateProjectEvent());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        try {/*progDailog.dismiss();*/} catch (Exception e) {
                            e.printStackTrace();
                        }
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        //new MessagesDialog(FilterActivity.this,"Фильтр", "Ошибка сохранения настроек фильтра.\nПроверьте соединение и повторите попытку.").show();
                    }
                });

            }
        });



       /* mAdapter.SetOnItemClickListener(new FilterListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                pos = position;

                mText.setText("Вы действительно хотите удалить категорию \"" + categoriesMainList.get(pos).getCatTitle() + "\"?");
                deleteCategory.show();
            }
        });

        */


        /*mainList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                pos=position;

                mText.setText("Вы действительно хотите удалить категорию \""+categoriesMainList.get(pos).getCatTitle()+"\"?");
                deleteCategory.show();


            }
        });*/




    }

    @Override
    public void onResume(){
        super.onResume();
        Appodeal.hide(this, Appodeal.BANNER_VIEW);
        //Appodeal.setBannerViewId(R.id.appodealBannerView);
        //Appodeal.show(this, Appodeal.BANNER_VIEW);
    }
}

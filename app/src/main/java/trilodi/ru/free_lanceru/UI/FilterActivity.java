package trilodi.ru.free_lanceru.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;

import java.util.ArrayList;

import trilodi.ru.free_lanceru.Adapters.CatGroupAdapter;
import trilodi.ru.free_lanceru.Adapters.CategoriesAdapter;
import trilodi.ru.free_lanceru.Adapters.FilterListAdapter;
import trilodi.ru.free_lanceru.Components.DBOpenHelper;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.CatGroup;
import trilodi.ru.free_lanceru.Models.Categories;
import trilodi.ru.free_lanceru.R;

public class FilterActivity extends ActionBarActivity {

    Switch filter_enabler;

    LinearLayout user_filter;

    boolean filter_enable=false;

    EditText keywords;

    String filterID="0";

    TextView mText;
    ListView categoriesList;

    private RecyclerView filterRecyclerView;
    private FilterListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList catGIDs=new ArrayList();
    ArrayList catIDs=new ArrayList();
    ArrayList<Categories> categoriesMainList = new ArrayList<Categories>();
    ArrayList<CatGroup> catGroups = new ArrayList<CatGroup>();

    ArrayList<Categories> categories = new ArrayList<Categories>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

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
                //add_cat.setVisibility(View.GONE);
                filter_enable=true;
            }else{
                filter_enabler.setChecked(false);
                user_filter.setVisibility(View.VISIBLE);
                //add_cat.setVisibility(View.VISIBLE);
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
                    //add_cat.setVisibility(View.GONE);
                    filter_enable=true;
                }else{
                    filter_enabler.setChecked(false);
                    user_filter.setVisibility(View.VISIBLE);
                    //add_cat.setVisibility(View.VISIBLE);
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


        final Dialog catGroupDialog = new Dialog(this);
        catGroupDialog.setContentView(R.layout.dialog_cat_groups_layout);
        catGroupDialog.setTitle("Группы категорий");
        catGroupDialog.setCancelable(true);

        CatGroupAdapter cga=new CatGroupAdapter(catGroupDialog.getContext(),catGroups);
        ListView catGroupList = (ListView)catGroupDialog.findViewById(R.id.listView7);
        catGroupList.setAdapter(cga);


        final Dialog categoriesDialog = new Dialog(this);
        categoriesDialog.setContentView(R.layout.dialog_cat_groups_layout);
        categoriesDialog.setTitle("Категории");
        categoriesDialog.setCancelable(true);

        final Dialog deleteCategory = new Dialog(this);
        deleteCategory.setContentView(R.layout.dialog_message);
        deleteCategory.setTitle("Удаление категории");
        deleteCategory.setCancelable(true);

        mText=(TextView)deleteCategory.findViewById(R.id.textView27);


        categoriesList = (ListView)categoriesDialog.findViewById(R.id.listView7);


        Button add=(Button)findViewById(R.id.sendButton);
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
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                categories = new ArrayList<Categories>();
                catGroupDialog.dismiss();
                Cursor c = Config.db.query("category", null, "category_group_id="+catGroups.get(position).getCatId(), null, null, null, "title ASC");
                if (c.moveToFirst()) {
                    int catId = c.getColumnIndex("id");
                    int catTitle = c.getColumnIndex("title");
                    do{
                        System.out.println(c.getString(catTitle));
                        categories.add(new Categories(c.getString(catId),c.getString(catTitle)));
                    }while (c.moveToNext());
                }
                c.close();

                CategoriesAdapter ca=new CategoriesAdapter(categoriesDialog.getContext(),categories);
                categoriesList.setAdapter(ca);

                categoriesDialog.show();
            }
        });



        categoriesList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                System.out.println(categories.get(position).getCatTitle());

                categoriesMainList.add(new Categories(categories.get(position).getCatId(),categories.get(position).getCatTitle()));
                //FilterListAdapter ca=new FilterListAdapter(FilterActivity.this,categoriesMainList);
                //mainList.setAdapter(ca);
                mAdapter = new FilterListAdapter(categoriesMainList);
                filterRecyclerView.setItemAnimator(new DefaultItemAnimator());
                filterRecyclerView.setAdapter(mAdapter);

                categoriesDialog.dismiss();
            }
        });

        /*mainList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                pos=position;

                mText.setText("Вы действительно хотите удалить категорию \""+categoriesMainList.get(pos).getCatTitle()+"\"?");
                deleteCategory.show();


            }
        });


        Button ok=(Button)deleteCategory.findViewById(R.id.okay);
        Button cancel=(Button)deleteCategory.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriesMainList.remove(pos);
                FilterListAdapter ca=new FilterListAdapter(FilterActivity.this,categoriesMainList);
                mainList.setAdapter(ca);
                deleteCategory.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory.dismiss();
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

package trilodi.ru.free_lanceru.UI;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import trilodi.ru.free_lanceru.Components.BusProvider;
import trilodi.ru.free_lanceru.Components.DBOpenHelper;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.Project;
import trilodi.ru.free_lanceru.Models.User;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

public class SplashScreenActivity extends ActionBarActivity {

    TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Config.dbHelper=new DBOpenHelper(this);
        Config.db = Config.dbHelper.getWritableDatabase();

        progressText = (TextView) findViewById(R.id.textView2);

        SharedPreferences localEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(localEditor.getBoolean("first_launch",true)){
            progressText.setText("Первый запуск....");
            loadSettings();
        }else if(!localEditor.getString("login","").equals("")){
            progressText.setText("Авторизация....");
            login(localEditor.getString("login", ""),localEditor.getString("password", ""));
        }
    }

    private void login(String login, final String password){
        String hash = Config.getMd5Hash(password+""+password);
        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("method", "users_signin");
        localRequestParams.put("username", login);
        localRequestParams.put("password", hash);


        NetManager.getInstance(this).post(localRequestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String str = new String(responseBody, "UTF-8");
                    //System.out.println(str);
                    JSONObject response = new JSONObject(str);
                    if (Integer.parseInt(response.get("error").toString()) == 0) {
                        Config.myUser = new User(response.getJSONObject("data"));


                        SharedPreferences.Editor localEditor2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        localEditor2.putString("login",Config.myUser.username);
                        localEditor2.putString("password", password);
                        localEditor2.putLong("login_time", (System.currentTimeMillis() / 1000L));
                        localEditor2.putString("id", Config.myUser.id);
                        localEditor2.commit();
                        progressText.setText("Загрузка проектов....");
                        loadProjects();

                    }else{
                        Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                        SplashScreenActivity.this.finish();
                    }

                } catch (Exception e) {
                    Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainActivity);
                    SplashScreenActivity.this.finish();
                }


            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
                mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainActivity);
                SplashScreenActivity.this.finish();
            }
        });
    }

    private void loadProjects()
    {


        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("method", "projects_list");
        localRequestParams.put("page", 1);
        NetManager.getInstance(this).post(localRequestParams, new AsyncHttpResponseHandler()
        {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String str = new String(responseBody, "UTF-8");

                    JSONObject localJSONObject = new JSONObject(str);
                    JSONArray ProjectsList = localJSONObject.getJSONObject("data").getJSONArray("projects_list");

                    ArrayList<Project> projects = new ArrayList<Project>();

                    try{
                        for (int i=0; i<ProjectsList.length(); i++){
                            //this.Project(projectsArray.getJSONObject(i));
                            projects.add(new Project(ProjectsList.getJSONObject(i)));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    BusProvider.getInstance().post(projects);

                    Intent mainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                    mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainActivity);
                    SplashScreenActivity.this.finish();


                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(){
                try {
                    //progDailog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //new MessagesDialog(getActivity(),"Проекты", "Во время загрузки списка проектов произошла ошибка соединения.\nПроверьте соединение и повторите попытку.").show();
            }
        });
        //setRefreshActionButtonState(true);
    }

    private void loadSettings(){
        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("method", "settings_get");

        progressText.setText("Загрузка настроек приложения...");

        NetManager.getInstance(this).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //progDailog.dismiss();
                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);

                    JSONObject settings = new JSONObject(str);
                    if(Integer.parseInt(settings.get("error").toString())==0){
                        SharedPreferences.Editor localEditor2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        localEditor2.putBoolean("first_launch",false);
                        localEditor2.commit();

                        progressText.setText("Сохранение настроек....");

                        new updateSettings().execute(str);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish() {
                try {
                    //progDailog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //new MessagesDialog(MainActivity.this, "Настройки системы", "Во время загрузки настроек было утеряно соединение с интернетом\nПроверьте соединение").show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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

    public class updateSettings extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressText.setText("Настройки загружены....");

            Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivity);
            SplashScreenActivity.this.finish();
        }

        @Override
        protected String doInBackground(String...paramArrayOfString) {
            try {
                JSONObject settings=new JSONObject(paramArrayOfString[0]);

                JSONArray citiesArray=settings.getJSONObject("data").getJSONObject("settings").getJSONArray("cities");
                for(int i=0;i<citiesArray.length();i++){
                    JSONObject city=citiesArray.getJSONObject(i);
                    Cursor cc =Config.db.query("city", null, "id="+city.get("id").toString(), null, null, null, null);
                    if (!cc.moveToFirst()) {
                        ContentValues cv = new ContentValues();
                        cv.put("id", city.get("id").toString());
                        cv.put("status", city.get("status").toString());
                        cv.put("update_time", city.get("update_time").toString());
                        cv.put("sequence", city.get("sequence").toString());
                        cv.put("title", city.get("title").toString());
                        cv.put("create_time", city.get("create_time").toString());
                        cv.put("country_id", city.get("country_id").toString());
                        Config.db.insert("city", null, cv);
                    }
                    cc.close();
                }

                JSONArray coutriesArray=settings.getJSONObject("data").getJSONObject("settings").getJSONArray("countries");
                for(int i=0;i<coutriesArray.length();i++){
                    JSONObject country=coutriesArray.getJSONObject(i);
                    Cursor cc =Config.db.query("country", null, "id="+country.get("id").toString(), null, null, null, null);
                    if (!cc.moveToFirst()) {
                        ContentValues cv = new ContentValues();
                        cv.put("id", country.get("id").toString());
                        cv.put("status", country.get("status").toString());
                        cv.put("update_time", country.get("update_time").toString());
                        cv.put("sequence", country.get("sequence").toString());
                        cv.put("title", country.get("title").toString());
                        cv.put("create_time", country.get("create_time").toString());
                        Config.db.insert("country", null, cv);
                    }
                    cc.close();
                }

                JSONObject filterObj=settings.getJSONObject("data").getJSONObject("settings").getJSONObject("filter");
                ContentValues cv = new ContentValues();
                cv.put("enabled", filterObj.get("enabled").toString());
                cv.put("keyword", filterObj.get("keyword").toString());
                Config.db.insert("filter", null, cv);

                try{
                    JSONArray filterItems = filterObj.getJSONArray("items");
                    for(int i=0;i<filterItems.length();i++){
                        JSONObject fItem=filterItems.getJSONObject(i);
                        cv = new ContentValues();
                        cv.put("filter_id", fItem.get("filter_id").toString());
                        cv.put("category_group_id", fItem.get("category_group_id").toString());
                        cv.put("category_id", fItem.get("category_id").toString());
                        Config.db.insert("filter_item", null, cv);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                JSONArray categoriesArray=settings.getJSONObject("data").getJSONObject("settings").getJSONArray("categories");
                for(int i=0;i<categoriesArray.length();i++){
                    JSONObject categories=categoriesArray.getJSONObject(i);
                    Cursor cc =Config.db.query("category", null, "id="+categories.get("id").toString(), null, null, null, null);
                    if (!cc.moveToFirst()) {
                        cv = new ContentValues();
                        cv.put("id", categories.get("id").toString());
                        cv.put("status", categories.get("status").toString());
                        cv.put("update_time", categories.get("update_time").toString());
                        cv.put("sequence", categories.get("sequence").toString());
                        cv.put("title", categories.get("title").toString());
                        cv.put("create_time", categories.get("create_time").toString());
                        cv.put("category_group_id", categories.get("categories_group_id").toString());
                        Config.db.insert("category", null, cv);
                    }
                    cc.close();
                }

                JSONArray categoryGroupArray=settings.getJSONObject("data").getJSONObject("settings").getJSONArray("categories_group");
                for(int i=0;i<categoryGroupArray.length();i++){
                    JSONObject categoryGroup=categoryGroupArray.getJSONObject(i);
                    Cursor cc =Config.db.query("category_group", null, "id="+categoryGroup.get("id").toString(), null, null, null, null);
                    if (!cc.moveToFirst()) {
                        cv = new ContentValues();
                        cv.put("id", categoryGroup.get("id").toString());
                        cv.put("status", categoryGroup.get("status").toString());
                        cv.put("update_time", categoryGroup.get("update_time").toString());
                        cv.put("sequence", categoryGroup.get("sequence").toString());
                        cv.put("title", categoryGroup.get("title").toString());
                        cv.put("create_time", categoryGroup.get("create_time").toString());
                        Config.db.insert("category_group", null, cv);
                    }
                    cc.close();
                }

                JSONObject pushObj=settings.getJSONObject("data").getJSONObject("settings").getJSONObject("push");

                try{
                    SharedPreferences.Editor pushPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    pushPref.putInt("project_select_performer",  Integer.parseInt(pushObj.get("project_select_performer").toString()));
                    pushPref.putInt("project_select_reject",     Integer.parseInt(pushObj.get("project_select_reject").toString()));
                    pushPref.putInt("projects_new",              Integer.parseInt(pushObj.get("projects_new").toString()));
                    pushPref.putInt("project_response_new",      Integer.parseInt(pushObj.get("project_response_new").toString()));
                    pushPref.putInt("message_new",               Integer.parseInt(pushObj.get("message_new").toString()));
                    pushPref.putInt("project_select_candidate",  Integer.parseInt(pushObj.get("project_select_candidate").toString()));
                    pushPref.commit();
                }catch (Exception e){
                    e.printStackTrace();
                }


            } catch (Exception localException) {
                localException.printStackTrace();
            }
            return null;
        }



    }
}

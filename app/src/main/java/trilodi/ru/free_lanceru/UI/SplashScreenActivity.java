package trilodi.ru.free_lanceru.UI;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import trilodi.ru.free_lanceru.Components.DBOpenHelper;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.Messages;
import trilodi.ru.free_lanceru.Models.Project;
import trilodi.ru.free_lanceru.Models.User;
import trilodi.ru.free_lanceru.Models.UserReview;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

public class SplashScreenActivity extends ActionBarActivity {

    TextView progressText;

    String upTime="0";
    Map<String, String> userIdsList = new HashMap<String, String>();
    Map<String, String> userIdsListNotSet = new HashMap<String, String>();

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
        }else{
            Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivity);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            SplashScreenActivity.this.finish();
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
                        localEditor2.putBoolean("first_launch_not_login", false);
                        localEditor2.commit();

                        userGet();

                    }else{
                        Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        SplashScreenActivity.this.finish();
                    }

                } catch (Exception e) {
                    Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainActivity);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                SplashScreenActivity.this.finish();
            }
        });

    }

    public void userGet(){

        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("id", Config.myUser.id);
        localRequestParams.put("method", "users_get");
        NetManager.getInstance(this).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);

                    JSONObject resp = new JSONObject(str);
                    if (resp.get("error").toString().equals("0")) {
                        //Config.myUser = new User(resp.getJSONObject("data").getJSONObject("user"));

                        Config.myUser.rating = resp.getJSONObject("data").getJSONObject("user").getInt("rating");

                        for(int i=0; i<resp.getJSONObject("data").getJSONObject("user").getJSONArray("reviews").length();i++){
                            JSONObject review = resp.getJSONObject("data").getJSONObject("user").getJSONArray("reviews").getJSONObject(i);
                            Config.myUser.reviews.add(new UserReview(review));
                        }

                        loadProjects();

                    }else{
                        Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        SplashScreenActivity.this.finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainActivity);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                SplashScreenActivity.this.finish();
            }
        });
    }

    private void loadProjects()
    {
        progressText.setText("Загрузка проектов....");

        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("method", "projects_list");
        localRequestParams.put("page", 1);
        NetManager.getInstance(this).post(localRequestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);

                    JSONObject localJSONObject = new JSONObject(str);
                    JSONArray ProjectsList = localJSONObject.getJSONObject("data").getJSONArray("projects_list");

                    ArrayList<Project> projects = new ArrayList<Project>();

                    try {
                        for (int i = 0; i < ProjectsList.length(); i++) {
                            //this.Project(projectsArray.getJSONObject(i));
                            projects.add(new Project(ProjectsList.getJSONObject(i)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ProjectsListFragment.projects = projects;
                    /*
                    Intent mainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                    mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainActivity);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    SplashScreenActivity.this.finish();
                    */



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                loadMEssages();
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

        progressText.setText("Загрузка настроек приложения...");

        try{
            String str="";
            StringBuffer buf = new StringBuffer();
            InputStream is = getAssets().open("settings_data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is!=null) {
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n" );
                }
            }
            is.close();
            SharedPreferences.Editor localEditor2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            localEditor2.putBoolean("first_launch",false);
            localEditor2.commit();

            progressText.setText("Сохранение настроек....");

            new updateSettings().execute(buf.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadMEssages(){

        progressText.setText("Загрузка сообщений...");

        Cursor c = null;
        c=Config.db.query("message", null, null, null, null, null, "update_time DESC");

        if (c.moveToFirst()) {
            int ut = c.getColumnIndex("update_time");

            upTime=c.getString(ut);

        }
        c.close();

        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("last_update", upTime);
        localRequestParams.put("method", "messages_list");
        NetManager.getInstance(this).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);

                    progressText.setText("Сохранение сообщений...");

                    JSONObject messages = new JSONObject(str);

                    new updateMEssages().execute(messages.toString());

                } catch (Exception e) {
                    loadUserList();
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
                Intent mainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainActivity);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                SplashScreenActivity.this.finish();
                super.onFinish();
            }
        });
    }

    private void loadUserList(){

        userIdsList.clear();
        userIdsListNotSet.clear();

        progressText.setText("Загрузка списка чатов...");
        new preLoaderMessadge().execute("");
    }

    public class updateMEssages extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            loadUserList();
        }

        @Override
        protected String doInBackground(String...paramArrayOfString) {
            try {
                JSONObject messages=new JSONObject(paramArrayOfString[0]);

                if (messages.get("error").toString().equals("0")) {
                    JSONArray list = messages.getJSONObject("data").getJSONArray("list");
                    if (list.length() > 0) {
                        for (int i = 0; i < list.length(); i++) {

                            Messages message = new Messages(list.getJSONObject(i));
                            Cursor c = Config.db.query("message", null, "id=?", new String[]{message.id}, null, null, "update_time DESC");
                            if (!c.moveToFirst()) {
                                ContentValues cv = new ContentValues();
                                cv.put("create_time", message.create_time);
                                cv.put("update_time", message.update_time);
                                cv.put("from_id", message.from_id);
                                cv.put("to_id", message.to_id);
                                cv.put("text", message.text);
                                cv.put("status", message.status);
                                cv.put("read", message.read);
                                cv.put("id", message.id);
                                Config.db.insert("message", null, cv);
                            }
                            c.close();
                        }

                    }
                }


            } catch (Exception localException) {
                localException.printStackTrace();
            }
            return null;
        }
    }

    public class updateUsers extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Intent mainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivity);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            SplashScreenActivity.this.finish();
        }

        @Override
        protected String doInBackground(String...paramArrayOfString) {
            try {
                JSONObject userData=new JSONObject(paramArrayOfString[0]);

                if (userData.get("error").toString().equals("0")) {
                    JSONArray users = userData.getJSONObject("data").getJSONArray("users");

                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        Cursor c = Config.db.query("user", null, "id=?", new String[]{user.get("id").toString()}, null, null, "update_time DESC");
                        if (c.moveToFirst()) {
                            ContentValues cv = new ContentValues();
                            cv.put("create_time", user.get("create_time").toString());
                            cv.put("update_time", user.get("update_time").toString());
                            cv.put("status", user.get("status").toString());
                            cv.put("username", user.get("username").toString());
                            cv.put("firstname", user.get("firstname").toString());
                            cv.put("lastname", user.get("lastname").toString());
                            cv.put("pro", user.get("pro").toString());
                            cv.put("verified", user.get("verified").toString());
                            cv.put("role", user.get("role").toString());
                            cv.put("spec", user.get("spec").toString());
                            JSONObject avatarURLObject = user.getJSONObject("avatar");
                            cv.put("avatar_url", avatarURLObject.get("url").toString() + "f_" + avatarURLObject.get("file").toString());
                            Config.db.update("user", cv, "id=?",new String[]{user.get("id").toString()});
                        }else {
                            ContentValues cv = new ContentValues();
                            cv.put("id", user.get("id").toString());
                            cv.put("create_time", user.get("create_time").toString());
                            cv.put("update_time", user.get("update_time").toString());
                            cv.put("status", user.get("status").toString());
                            cv.put("username", user.get("username").toString());
                            cv.put("firstname", user.get("firstname").toString());
                            cv.put("lastname", user.get("lastname").toString());
                            cv.put("pro", user.get("pro").toString());
                            cv.put("verified", user.get("verified").toString());
                            cv.put("role", user.get("role").toString());
                            cv.put("spec", user.get("spec").toString());
                            JSONObject avatarURLObject = user.getJSONObject("avatar");
                            cv.put("avatar_url", avatarURLObject.get("url").toString() + "f_" + avatarURLObject.get("file").toString());
                            Config.db.insert("user", null, cv);
                        }
                    }
                }


            } catch (Exception localException) {
                localException.printStackTrace();
            }
            return null;
        }
    }

    public class preLoaderMessadge extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(userIdsListNotSet.size()>0) {
                RequestParams localRequestParams = new RequestParams();
                localRequestParams.put("method", "users_list");

                for(Map.Entry<String, String> entry : userIdsListNotSet.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    localRequestParams.put("ids[" + key + "]", value);
                }

                NetManager.getInstance(SplashScreenActivity.this).post(localRequestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String str = new String(responseBody, "UTF-8");
                            System.out.println(str);

                            JSONObject userData = new JSONObject(str);

                            new updateUsers().execute(userData.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {super.onFinish();}

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Intent mainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        SplashScreenActivity.this.finish();
                    }
                });
            }else {
                Intent mainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainActivity);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                SplashScreenActivity.this.finish();
            }

        }

        @Override
        protected String doInBackground(String...paramArrayOfString) {
            //ArrayList allUsers = new ArrayList();

            Cursor cc =Config.db.query("user", null, null, null, null, null, null);
            if (cc.moveToFirst()) {
                int uID = cc.getColumnIndex("id");
                do{
                    //allUsers.add(cc.getString(uID));
                    userIdsList.put(cc.getString(uID),cc.getString(uID));
                }while(cc.moveToNext());
            }
            cc.close();

            //ArrayList mUList= new ArrayList();
            Cursor c =Config.db.query("message", null, null, null, null, null, "update_time DESC");
            if (c.moveToFirst()) {
                int uID = c.getColumnIndex("from_id");
                int toID = c.getColumnIndex("to_id");
                do{
                    if(!userIdsList.containsKey(c.getString(toID))){
                        userIdsListNotSet.put(c.getString(toID),c.getString(toID));
                    }
                    if(!userIdsList.containsKey(c.getString(uID))){
                        userIdsListNotSet.put(c.getString(uID),c.getString(uID));
                    }
                }while(c.moveToNext());
            }
            c.close();
            return null;
        }
    }

    public class updateSettings extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressText.setText("Настройки загружены....");

            Intent mainActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivity);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            SplashScreenActivity.this.finish();
        }

        @Override
        protected String doInBackground(String...paramArrayOfString) {
            try {
                JSONObject settings=new JSONObject(paramArrayOfString[0]);

                JSONArray citiesArray=settings.getJSONArray("cities");
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

                JSONArray coutriesArray=settings.getJSONArray("countries");
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

                JSONObject filterObj=settings.getJSONObject("filter");
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

                JSONArray categoriesArray=settings.getJSONArray("categories");
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

                JSONArray categoryGroupArray=settings.getJSONArray("categories_group");
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

                JSONObject pushObj=settings.getJSONObject("push");

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

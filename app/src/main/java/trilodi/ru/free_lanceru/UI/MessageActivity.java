package trilodi.ru.free_lanceru.UI;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.appodeal.ads.Appodeal;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import trilodi.ru.free_lanceru.Adapters.MessageListAdapter;
import trilodi.ru.free_lanceru.Components.BusProvider;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.Messages;
import trilodi.ru.free_lanceru.Models.User;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

public class MessageActivity extends ActionBarActivity {

    public static String chatId = "0";
    public static String from_id = "0";
    public static String to_id = "0";

    ArrayList<Messages> messages = new ArrayList<Messages>();
    Map<String, User> users = new HashMap<String, User>();

    private RecyclerView messagesRecyclerView;
    private MessageListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SwipeRefreshLayout refreshLayout;

    ImageView backButton, sendButton;

    EditText messageText;

    ProgressBarCircularIndeterminate progressIndicator;

    String upTime = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);



        progressIndicator = (ProgressBarCircularIndeterminate) findViewById(R.id.dialogProgress);
        progressIndicator.setVisibility(View.GONE);

        messagesRecyclerView = (RecyclerView) findViewById(R.id.projectList);
        messagesRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(messagesRecyclerView.getContext(),LinearLayoutManager.VERTICAL,true);
        messagesRecyclerView.setLayoutManager(mLayoutManager);

        backButton = (ImageView) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageActivity.this.finish();
            }
        });

        messageText = (EditText) findViewById(R.id.messageText);
        sendButton = (ImageView) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMEssage();
            }
        });

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);


        loadLocaleUser(from_id);
        loadLocaleUser(to_id);
        loadLocaleMessage();




        mAdapter = new MessageListAdapter(messages,users);
        messagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messagesRecyclerView.setAdapter(mAdapter);
        refreshLayout.setRefreshing(false);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMEssages();
            }
        });

    }

    public void sendMEssage(){
        progressIndicator.setVisibility(View.VISIBLE);
        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("method", "messages_send");
        localRequestParams.put("to_id", chatId);
        localRequestParams.put("text", messageText.getText().toString());

        messageText.setText("");

        NetManager.getInstance(this).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        String str = new String(responseBody, "UTF-8");
                        System.out.println(str);

                        JSONObject localJSONObject = new JSONObject(str);

                        if(!localJSONObject.get("error").toString().equals("0")){
                            Intent splash = new Intent(MessageActivity.this,SplashScreenActivity.class);
                            splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(splash);
                            finish();
                        }

                        JSONObject mess = localJSONObject.getJSONObject("data").getJSONObject("message");

                        ContentValues cv = new ContentValues();
                        cv.put("create_time", mess.get("create_time").toString());
                        cv.put("update_time", mess.get("update_time").toString());
                        cv.put("from_id", mess.get("from_id").toString());
                        cv.put("to_id", mess.get("to_id").toString());
                        cv.put("text", mess.get("text").toString());
                        cv.put("status", mess.get("status").toString());
                        cv.put("read", mess.get("read").toString());
                        cv.put("id", mess.get("id").toString());
                        Config.db.insert("message", null, cv);

                        ArrayList<Boolean> b = new ArrayList<Boolean>();
                        b.add(true);

                        BusProvider.getInstance().post(b);

                        messages.clear();
                        users.clear();

                        progressIndicator.setVisibility(View.GONE);

                        loadLocaleUser(from_id);
                        loadLocaleUser(to_id);
                        loadLocaleMessage();

                        mAdapter = new MessageListAdapter(messages, users);
                        messagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        messagesRecyclerView.setAdapter(mAdapter);
                        refreshLayout.setRefreshing(false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //new MessagesDialog(ReadMessageActivity.this,"Сообщения", "Во время отправки сообщения произошла ошибка соединения.\nПроверьте соединение и повторите попытку.").show();
            }
        });
    }

    private void loadLocaleMessage(){

        progressIndicator.setVisibility(View.VISIBLE);
        Cursor c =Config.db.query("message", null, "from_id=? OR to_id=?", new String[]{chatId,chatId}, null, null, "create_time DESC");
        if (c.moveToFirst()) {
            int id = c.getColumnIndex("id");
            int create_time = c.getColumnIndex("create_time");
            int update_time = c.getColumnIndex("update_time");
            int from_id = c.getColumnIndex("from_id");
            int to_id = c.getColumnIndex("to_id");
            int read = c.getColumnIndex("read");
            int text = c.getColumnIndex("text");
            int status = c.getColumnIndex("status");
            do{
                Messages message = new Messages();
                message.id = c.getString(id);
                message.create_time = c.getString(create_time);
                message.update_time = c.getString(update_time);
                message.from_id = c.getString(from_id);
                message.to_id = c.getString(to_id);
                message.read = c.getString(read);
                message.text = c.getString(text);
                message.status = c.getString(status);
                messages.add(message);
            }while(c.moveToNext());
        }
        c.close();
        progressIndicator.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
        if(messages.size()==0){
            Appodeal.hide(this, Appodeal.BANNER_VIEW);
        }
    }

    private void loadLocaleUser(String user_id){
        progressIndicator.setVisibility(View.VISIBLE);
        Cursor c = Config.db.query("user", null, "id=?", new String[]{user_id}, null, null, null);
        if (c.moveToFirst()) {

            int create_time = c.getColumnIndex("create_time");
            int update_time = c.getColumnIndex("update_time");
            int status = c.getColumnIndex("status");
            int username = c.getColumnIndex("username");
            int firstname = c.getColumnIndex("firstname");
            int lastname = c.getColumnIndex("lastname");
            int pro = c.getColumnIndex("pro");
            int verified = c.getColumnIndex("verified");
            int role = c.getColumnIndex("role");
            int spec = c.getColumnIndex("spec");
            int avatar_url = c.getColumnIndex("avatar_url");

            System.out.println(c.getString(username));

            User chat_user = new User(user_id,c.getString(create_time), c.getString(update_time),c.getString(status),c.getString(username), c.getString(firstname), c.getString(lastname), c.getString(pro), c.getString(verified), c.getString(role),c.getString(spec),c.getString(avatar_url));
            users.put(user_id, chat_user);
        }
        c.close();
        progressIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onResume(){
        super.onResume();
        Appodeal.hide(this, Appodeal.BANNER_VIEW);
        //Appodeal.setBannerViewId(R.id.appodealBannerView);
        //Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

    public void loadMEssages(){

        progressIndicator.setVisibility(View.VISIBLE);

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

                    JSONObject messages = new JSONObject(str);

                    new updateMEssages().execute(messages.toString());

                } catch (Exception e) {
                    loadUserList();
                }
            }

            @Override
            public void onFinish() {
                try {
                    progressIndicator.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                super.onFinish();

            }
        });
    }

    private void loadUserList(){

        progressIndicator.setVisibility(View.VISIBLE);

        //ArrayList allUsers = new ArrayList();
        Map<String, String> userIdsList = new HashMap<String, String>();
        Map<String, String> userIdsListNotSet = new HashMap<String, String>();
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


        if(userIdsListNotSet.size()>0) {
            RequestParams localRequestParams = new RequestParams();
            localRequestParams.put("method", "users_list");

            for(Map.Entry<String, String> entry : userIdsListNotSet.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                localRequestParams.put("ids[" + key + "]", value);
            }

            NetManager.getInstance(this).post(localRequestParams, new AsyncHttpResponseHandler() {
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
                public void onFinish() {
                    super.onFinish();
                    progressIndicator.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressIndicator.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                }
            });
        }else {
            progressIndicator.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
        }

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

            messages.clear();
            users.clear();

            progressIndicator.setVisibility(View.GONE);

            loadLocaleUser(from_id);
            loadLocaleUser(to_id);
            loadLocaleMessage();

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

}

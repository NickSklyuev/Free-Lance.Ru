package trilodi.ru.free_lance.UI;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appodeal.ads.Appodeal;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.analytics.HitBuilders;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import trilodi.ru.free_lance.Adapters.ChatsListAdapter;
import trilodi.ru.free_lance.Components.BusProvider;
import trilodi.ru.free_lance.Components.DBOpenHelper;
import trilodi.ru.free_lance.Config;
import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lance.Models.Chats;
import trilodi.ru.free_lance.Models.Messages;
import trilodi.ru.free_lance.Models.User;
import trilodi.ru.free_lance.Network.NetManager;
import trilodi.ru.free_lanceru.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessagesFragmant.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessagesFragmant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragmant extends Fragment {

    private ImageView MenuButton;

    ProgressBarCircularIndeterminate progerssIndicator;

    private OnFragmentInteractionListener mListener;

    private RecyclerView messagesRecyclerView;
    private ChatsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SwipeRefreshLayout refreshLayout;

    ArrayList<Chats> chatArray = new ArrayList<Chats>();

    String upTime="0";


    @Subscribe
    public void onUpdateResponses(ArrayList<Boolean> event){
        try{
            if (event.get(0)==true){
                //chatArray.clear();
                new showChats().execute("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessagesFragmant.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragmant newInstance() {
        MessagesFragmant fragment = new MessagesFragmant();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MessagesFragmant() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.dbHelper=new DBOpenHelper(getActivity());
        Config.db = Config.dbHelper.getWritableDatabase();
        BusProvider.getInstance().register(this);
        FreeLanceApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open").setLabel("Сообщения").build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_messages_fragmant, container, false);
        MenuButton = (ImageView) v.findViewById(R.id.MenuButton);
        MenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        progerssIndicator = (ProgressBarCircularIndeterminate) v.findViewById(R.id.dialogProgress);


        messagesRecyclerView = (RecyclerView) v.findViewById(R.id.projectList);
        messagesRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        messagesRecyclerView.setLayoutManager(mLayoutManager);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMEssages();
            }
        });

        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        new showChats().execute("");


        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class showChats extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mAdapter = new ChatsListAdapter(chatArray);
            messagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
            messagesRecyclerView.setAdapter(mAdapter);
            refreshLayout.setRefreshing(false);
            progerssIndicator.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String...paramArrayOfString) {
            Map<String, Chats> chatList = new HashMap<String, Chats>();
            ArrayList<String> chatIDS = new ArrayList<String>();
            chatArray = new ArrayList<Chats>();
            Cursor c =Config.db.query("message", null, null, null, null, null, "create_time DESC LIMIT 500");
            if (c.moveToFirst()) {
                int uID = c.getColumnIndex("from_id");
                int toID = c.getColumnIndex("to_id");
                int read = c.getColumnIndex("read");
                do{
                    int unreat = 0;



                    if (!Config.myUser.id.equals(c.getString(uID))){
                        if(c.getString(read).equals("0")){
                            unreat = 1;
                        }
                        if(!chatList.containsKey(c.getString(uID))){
                            chatList.put(c.getString(uID), new Chats(c.getString(uID),c.getString(toID),1,unreat,c.getString(uID), new User()));
                            chatIDS.add(c.getString(uID));
                        }else{
                            Chats chat = chatList.get(c.getString(uID));
                            chat.unreded+=unreat;
                            chat.messages+=1;
                            chatList.put(c.getString(uID), chat);
                        }

                    }else{
                        if(!chatList.containsKey(c.getString(toID))){
                            chatList.put(c.getString(toID), new Chats(c.getString(uID),c.getString(toID),1,unreat,c.getString(toID), new User()));
                            chatIDS.add(c.getString(toID));
                        }else{
                            Chats chat = chatList.get(c.getString(toID));
                            chat.unreded+=unreat;
                            chat.messages+=1;
                            chatList.put(c.getString(toID), chat);
                        }

                    }
                }while(c.moveToNext());
            }
            c.close();

            System.out.println(chatList);

            for(int i = 0; i<chatIDS.size(); i++) {
                //String key = entry.getKey();
                //System.out.println(key);
                //Chats value = entry.getValue();

                Chats chat = chatList.get(chatIDS.get(i));

                c = Config.db.query("user", null, "id=?", new String[]{chat.id}, null, null, null);
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

                    User chat_user = new User(chat.id,c.getString(create_time), c.getString(update_time),c.getString(status),c.getString(username), c.getString(firstname), c.getString(lastname), c.getString(pro), c.getString(verified), c.getString(role),c.getString(spec),c.getString(avatar_url));
                    chat.user = chat_user;
                    //chatList.put(key,value);
                    chatArray.add(chat);
                }
                c.close();

                if(i>=20){
                    break;
                }
            }
            return null;
        }
    }

    public void loadMEssages(){

        chatArray = new ArrayList<Chats>();
        progerssIndicator.setVisibility(View.VISIBLE);

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
        NetManager.getInstance(getActivity()).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);


                    JSONObject messages = new JSONObject(str);

                    if(!messages.get("error").toString().equals("0")){
                        Intent splash = new Intent(getActivity(),SplashScreenActivity.class);
                        splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(splash);
                        getActivity().finish();
                    }

                    new updateMEssages().execute(messages.toString());

                } catch (Exception e) {
                    loadUserList();
                }
            }

            @Override
            public void onFinish() {
                try {
                    progerssIndicator.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progerssIndicator.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadUserList(){

        progerssIndicator.setVisibility(View.VISIBLE);

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

            NetManager.getInstance(getActivity()).post(localRequestParams, new AsyncHttpResponseHandler() {
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
                    new showChats().execute("");
                }
            });
        }else {
            new showChats().execute("");
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

            new showChats().execute("");
            //refreshLayout.setRefreshing(false);
            //progerssIndicator.setVisibility(View.GONE);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume(){
        super.onResume();
        Appodeal.hide(getActivity(), Appodeal.BANNER_VIEW);
        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(getActivity(), Appodeal.BANNER_VIEW);
    }

}

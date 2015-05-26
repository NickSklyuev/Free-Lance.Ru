package trilodi.ru.free_lanceru.UI;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
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

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import trilodi.ru.free_lanceru.Adapters.ChatsListAdapter;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.Chats;
import trilodi.ru.free_lanceru.Models.User;
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

    Map<String, Chats> chatList = new HashMap<String, Chats>();
    ArrayList<Chats> chatArray = new ArrayList<Chats>();
    ArrayList<String> chatIDS = new ArrayList<String>();

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


        Cursor c =Config.db.query("message", null, null, null, null, null, "create_time DESC");
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
        }

        mAdapter = new ChatsListAdapter(chatArray);
        messagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messagesRecyclerView.setAdapter(mAdapter);
        refreshLayout.setRefreshing(false);


        progerssIndicator.setVisibility(View.GONE);

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

}

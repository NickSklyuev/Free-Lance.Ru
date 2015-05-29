package trilodi.ru.free_lanceru.UI;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import trilodi.ru.free_lanceru.Components.AvatarDrawable;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.User;
import trilodi.ru.free_lanceru.Models.UserReview;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener mListener;

    private ImageView backButton, avatarImage, proImage, verImage;

    TextView userName, ratingText, reviewText, contactsText, roleText, createdText, countryText;


    Picasso mPicasso;
    private com.squareup.picasso.Target loadtarget;

    User user;
    public static String userId = "0";

    AlertDialog dialog;
    View loadingView;

    ButtonFloat buttonFloat;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_alls, container, false);

        backButton = (ImageView) v.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();

            }
        });

        buttonFloat = (ButtonFloat) v.findViewById(R.id.buttonFloat);

        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = Config.db.query("user", null, "id=?", new String[]{user.id}, null, null, "update_time DESC");
                if (c.moveToFirst()) {
                    ContentValues cv = new ContentValues();
                    cv.put("create_time", String.valueOf(user.create_time));
                    cv.put("update_time", String.valueOf(user.update_time));
                    cv.put("status", String.valueOf(user.status));
                    cv.put("username", String.valueOf(user.username));
                    cv.put("firstname", String.valueOf(user.firstname));
                    cv.put("lastname", String.valueOf(user.lastname));
                    cv.put("pro", String.valueOf(user.pro));
                    cv.put("verified", String.valueOf(user.verified));
                    cv.put("role", String.valueOf(user.role));
                    cv.put("spec", String.valueOf(user.spec));
                    Map<String, String>  avatar_url= user.avatar;
                    cv.put("avatar_url", avatar_url.get("url").toString() + "f_" + avatar_url.get("file").toString());
                    Config.db.update("user", cv, "id=?",new String[]{user.id});
                }else {
                    ContentValues cv = new ContentValues();
                    cv.put("id", user.id);
                    cv.put("create_time", String.valueOf(user.create_time));
                    cv.put("update_time", String.valueOf(user.update_time));
                    cv.put("status", String.valueOf(user.status));
                    cv.put("username", String.valueOf(user.username));
                    cv.put("firstname", String.valueOf(user.firstname));
                    cv.put("lastname", String.valueOf(user.lastname));
                    cv.put("pro", String.valueOf(user.pro));
                    cv.put("verified", String.valueOf(user.verified));
                    cv.put("role", String.valueOf(user.role));
                    cv.put("spec", String.valueOf(user.spec));
                    Map<String, String>  avatar_url= user.avatar;
                    cv.put("avatar_url", avatar_url.get("url").toString() + "f_" + avatar_url.get("file").toString());
                    Config.db.insert("user", null, cv);
                }
                c.close();

                MessageActivity.chatId = user.id;
                MessageActivity.from_id = user.id;
                MessageActivity.to_id = Config.myUser.id;

                Intent messageActivity = new Intent(v.getContext(),MessageActivity.class);
                messageActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(messageActivity);
            }
        });


        avatarImage = (ImageView) v.findViewById(R.id.imageView2);
        proImage = (ImageView) v.findViewById(R.id.imageView6);
        verImage  =(ImageView) v.findViewById(R.id.imageView7);

        userName = (TextView) v.findViewById(R.id.textView7);
        ratingText = (TextView) v.findViewById(R.id.textView4);
        reviewText = (TextView) v.findViewById(R.id.textView6);
        roleText = (TextView) v.findViewById(R.id.textView16);

        createdText = (TextView) v.findViewById(R.id.textView20);
        countryText = (TextView) v.findViewById(R.id.textView19);

        mPicasso = Picasso.with(avatarImage.getContext());

        contactsText = (TextView) v.findViewById(R.id.contactsData);

        loadingView = getActivity().getLayoutInflater().inflate(R.layout.load_dialog_layout, null);
        ProgressBarCircularIndeterminate progresser = (ProgressBarCircularIndeterminate) loadingView.findViewById(R.id.dialogProgress);

        TextView dialogTitle = (TextView) loadingView.findViewById(R.id.dialogtitle);
        TextView dialogDescription = (TextView) loadingView.findViewById(R.id.dialogDescription);

        dialogTitle.setText(getResources().getString(R.string.PROFILE_DIALOG_TITLE));
        dialogDescription.setText(getResources().getString(R.string.PROFILE_DIALOG_TEXT));

        dialog = new AlertDialog.Builder(getActivity()).setView(loadingView).setCancelable(false).create();

        loadUser();

        return v;
    }

    public void renderUserData(){
        String contacts = "";

        for(Map.Entry<String, ArrayList<String>> entry : user.contatcs.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();



            for(int i=0;i<value.size();i++){
                //contacts+="\t\t"+value.get(i)+"\n";
                contacts+=""+key+": "+value.get(i)+"\n";
            }
        }

        contactsText.setText(contacts);

        String username = "";

        username = user.firstname;
        if(!user.lastname.equals("")){
            username = user.firstname+" "+user.lastname;
        }
        if(!user.username.equals("")){
            username = user.firstname+" "+user.lastname+" ("+user.username+")";
        }

        userName.setText(username.trim());

        AvatarDrawable avatarDrawable = null;
        avatarDrawable = new AvatarDrawable(user);
        avatarImage.setImageDrawable(avatarDrawable);

        if (loadtarget == null) {
            loadtarget = new com.squareup.picasso.Target()  {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap
                    avatarImage.setImageBitmap(roundImage(bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable){

                }
            };
        }

        if(!user.avatar.get("file").equals("")){
            mPicasso.load(user.avatar.get("url")+"f_"+user.avatar.get("file")).into(loadtarget);
        }

        try{
            ratingText.setText("Рейтинг: "+user.rating);
        }catch(Exception e){
            ratingText.setText("");
        }

        reviewText.setText("Отзывов: "+user.reviews.size());

        if(user.role == 2){
            roleText.setText("Работодатель");
        }
        if(user.role == 1){
            roleText.setText("Фрилансер");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        long timestamp = Long.parseLong(String.valueOf(user.create_time)) * 1000;
        java.util.Date netDate = (new java.util.Date(timestamp));

        createdText.setText(sdf.format(netDate));

        String CoutryCity = "";
        Cursor cc =Config.db.query("country", null, "id="+user.country_id, null, null, null, null);
        if (cc.moveToFirst()) {
            int tIndex=cc.getColumnIndex("title");
            CoutryCity=cc.getString(tIndex)+", ";
        }
        cc.close();

        cc =Config.db.query("city", null, "id="+user.city_id, null, null, null, null);
        if (cc.moveToFirst()) {
            int tIndex=cc.getColumnIndex("title");
            CoutryCity=CoutryCity+cc.getString(tIndex);
        }
        cc.close();


        countryText.setText(CoutryCity);

        if(user.pro==0){
            proImage.setVisibility(View.GONE);
        }

        if(user.verified == 0){
            verImage.setVisibility(View.GONE);
        }

        dialog.dismiss();
    }

    public void loadUser(){

        dialog.show();

        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("id", userId);
        localRequestParams.put("method", "users_get");
        NetManager.getInstance(getActivity()).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);

                    JSONObject resp = new JSONObject(str);
                    if (resp.get("error").toString().equals("0")) {
                        //Config.myUser = new User(resp.getJSONObject("data").getJSONObject("user"));
                        user = new User(resp.getJSONObject("data").getJSONObject("user"));

                        for(int i=0; i<resp.getJSONObject("data").getJSONObject("user").getJSONArray("reviews").length();i++){
                            JSONObject review = resp.getJSONObject("data").getJSONObject("user").getJSONArray("reviews").getJSONObject(i);
                            user.reviews.add(new UserReview(review));
                        }

                        JSONObject contactsObj=resp.getJSONObject("data").getJSONObject("user").getJSONObject("contacts");
                        String contactsData="";
                        Iterator<String> iter = contactsObj.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            ArrayList<String> cData = new ArrayList<String>();
                            try {
                                JSONArray conatcsItem=contactsObj.getJSONArray(key);
                                for(int i=0;i<conatcsItem.length();i++){
                                    if(conatcsItem.get(i).toString().equals("")){
                                        continue;
                                    }

                                    cData.add(conatcsItem.get(i).toString());

                                }
                                user.contatcs.put(key,cData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        renderUserData();

                    }else{
                        Intent splash = new Intent(getActivity(),SplashScreenActivity.class);
                        splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(splash);
                        getActivity().finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().finish();
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getActivity().finish();
            }
        });
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

    public Bitmap roundImage(Bitmap bm){
        //java.io.File image = new java.io.File(path);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        Bitmap bitmap = bm;

        Bitmap bitmapRounded = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(bitmapRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight())), 80, 80, paint);

        return bitmapRounded;
    }

    @Override
    public void onResume(){
        super.onResume();
        Appodeal.hide(getActivity(), Appodeal.BANNER_VIEW);
    }

}

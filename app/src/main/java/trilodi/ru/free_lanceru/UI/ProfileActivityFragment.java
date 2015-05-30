package trilodi.ru.free_lanceru.UI;

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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import trilodi.ru.free_lanceru.Components.AvatarDrawable;
import trilodi.ru.free_lanceru.Components.DBOpenHelper;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileActivityFragment extends Fragment {

    private ImageView MenuButton, avatarImage, proImage, verImage;

    TextView userName, ratingText, reviewText, contactsText, roleText, createdText, countryText;


    Picasso mPicasso;
    private com.squareup.picasso.Target loadtarget;

    Button portfolioButton;

    public ProfileActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.dbHelper=new DBOpenHelper(getActivity());
        Config.db = Config.dbHelper.getWritableDatabase();
    }

    public static ProfileActivityFragment newInstance() {
        ProfileActivityFragment fragment = new ProfileActivityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        portfolioButton = (Button) v.findViewById(R.id.sendButton);

        portfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PortfolioActivityFragment.user_id = Config.myUser.id;

                Intent portfolio = new Intent(getActivity(), PortfolioActivity.class);
                portfolio.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(portfolio);
            }
        });

        MenuButton = (ImageView) v.findViewById(R.id.MenuButton);
        MenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        avatarImage = (ImageView) v.findViewById(R.id.imageView2);
        proImage = (ImageView) v.findViewById(R.id.imageView6);
        verImage  =(ImageView) v.findViewById(R.id.imageView7);

        userName = (TextView) v.findViewById(R.id.textView7);
        ratingText = (TextView) v.findViewById(R.id.textView4);
        reviewText = (TextView) v.findViewById(R.id.textView6);
        roleText = (TextView) v.findViewById(R.id.textView16);

        reviewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewShowFragment.allReviews = Config.myUser.reviews;
                Intent rev = new Intent(getActivity(), ProfileReviewActivity.class);
                rev.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(rev);
            }
        });

        createdText = (TextView) v.findViewById(R.id.textView20);
        countryText = (TextView) v.findViewById(R.id.textView19);

        mPicasso = Picasso.with(avatarImage.getContext());

        contactsText = (TextView) v.findViewById(R.id.contactsData);

        String contacts = "";

        for(Map.Entry<String, ArrayList<String>> entry : Config.myUser.contatcs.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();



            for(int i=0;i<value.size();i++){
                //contacts+="\t\t"+value.get(i)+"\n";
                contacts+=""+key+": "+value.get(i)+"\n";
            }
        }

        contactsText.setText(contacts);

        String username = "";

        username = Config.myUser.firstname;
        if(!Config.myUser.lastname.equals("")){
            username = Config.myUser.firstname+" "+Config.myUser.lastname;
        }
        if(!Config.myUser.username.equals("")){
            username = Config.myUser.firstname+" "+Config.myUser.lastname+" ("+Config.myUser.username+")";
        }

        userName.setText(username.trim());

        AvatarDrawable avatarDrawable = null;
        avatarDrawable = new AvatarDrawable(Config.myUser);
        avatarImage.setImageDrawable(avatarDrawable);

        if (loadtarget == null) {
            loadtarget = new com.squareup.picasso.Target()  {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap
                    try{
                        avatarImage.setImageBitmap(roundImage(bitmap));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable){

                }
            };
        }

        if(!Config.myUser.avatar.get("file").equals("")){
            mPicasso.load(Config.myUser.avatar.get("url")+"f_"+Config.myUser.avatar.get("file")).into(loadtarget);
        }

        try{
            ratingText.setText("Рейтинг: "+Config.myUser.rating);
        }catch(Exception e){
            ratingText.setText("");
        }

        reviewText.setText("Отзывов: "+Config.myUser.reviews.size());

        if(Config.myUser.role == 2){
            roleText.setText("Работодатель");
        }
        if(Config.myUser.role == 1){
            roleText.setText("Фрилансер");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        long timestamp = Long.parseLong(String.valueOf(Config.myUser.create_time)) * 1000;
        java.util.Date netDate = (new java.util.Date(timestamp));

        createdText.setText(sdf.format(netDate));

        String CoutryCity = "";
        Cursor cc =Config.db.query("country", null, "id="+Config.myUser.country_id, null, null, null, null);
        if (cc.moveToFirst()) {
            int tIndex=cc.getColumnIndex("title");
            CoutryCity=cc.getString(tIndex)+", ";
        }
        cc.close();

        cc =Config.db.query("city", null, "id="+Config.myUser.city_id, null, null, null, null);
        if (cc.moveToFirst()) {
            int tIndex=cc.getColumnIndex("title");
            CoutryCity=CoutryCity+cc.getString(tIndex);
        }
        cc.close();


        countryText.setText(CoutryCity);


        if(Config.myUser.pro==0){
            proImage.setVisibility(View.GONE);
        }

        if(Config.myUser.verified == 0){
            verImage.setVisibility(View.GONE);
        }


        return v;
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

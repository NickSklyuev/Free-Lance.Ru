package trilodi.ru.free_lance.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.analytics.HitBuilders;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import trilodi.ru.free_lance.Components.UpdateProjectEvent;
import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lance.Models.Project;
import trilodi.ru.free_lance.Models.Responses;
import trilodi.ru.free_lance.Network.NetManager;
import trilodi.ru.free_lance.Adapters.FilesAdapter;
import trilodi.ru.free_lance.Components.AvatarDrawable;
import trilodi.ru.free_lance.Components.BusProvider;
import trilodi.ru.free_lance.Components.DBOpenHelper;
import trilodi.ru.free_lance.Components.UpdateResponsesEvent;
import trilodi.ru.free_lance.Config;
import trilodi.ru.free_lanceru.R;

public class ProjectActivity extends ActionBarActivity {

    Project project;
    ImageView avatar;
    TextView userName, onlineStatus, titleTExt, dateText, descrText, budgetText, responsesText;
    RelativeLayout only_pro, onlyver;
    ListView attachesList;
    com.gc.materialdesign.views.ButtonFloat writeResponse;

    Picasso mPicasso;
    private com.squareup.picasso.Target loadtarget;

    AlertDialog dialog;
    View loadingView;

    String[] currency={"USD","EURO","р."};
    String[] dimension={"","/Час","/День","/Месяц","/Проект"};

    @Subscribe
    public void onUpdateProject(UpdateProjectEvent event){
        loadProject();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        FreeLanceApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open").setLabel("Чтение проекта").build());

        Config.dbHelper=new DBOpenHelper(this);
        Config.db = Config.dbHelper.getWritableDatabase();

        BusProvider.getInstance().register(this);

        loadingView = getLayoutInflater().inflate(R.layout.load_dialog_layout, null);
        ProgressBarCircularIndeterminate progresser = (ProgressBarCircularIndeterminate) loadingView.findViewById(R.id.dialogProgress);

        TextView dialogTitle = (TextView) loadingView.findViewById(R.id.dialogtitle);
        TextView dialogDescription = (TextView) loadingView.findViewById(R.id.dialogDescription);

        dialogTitle.setText(getResources().getString(R.string.LOAD_PROJECT_DIALOG_TITLE));
        dialogDescription.setText(getResources().getString(R.string.LOAD_PROJECT_DIALOG_TEXT));

        dialog = new AlertDialog.Builder(this).setView(loadingView).setCancelable(false).create();
        dialog.show();
        avatar = (ImageView) findViewById(R.id.avatarImage);
        userName = (TextView) findViewById(R.id.userName);
        onlineStatus = (TextView) findViewById(R.id.online_status);
        descrText = (TextView) findViewById(R.id.descrText);
        budgetText = (TextView) findViewById(R.id.budgetText);
        responsesText = (TextView) findViewById(R.id.responsesText);

        LinearLayout profileData = (LinearLayout) findViewById(R.id.profileData);
        profileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment.userId = project.user.id;

                Intent userProfile = new Intent(v.getContext(), ProfileActivity.class);
                userProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(userProfile);
            }
        });

        attachesList = (ListView) findViewById(R.id.attachesList);

        writeResponse = (com.gc.materialdesign.views.ButtonFloat) findViewById(R.id.buttonFloat);

        writeResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addResponse = new Intent(ProjectActivity.this,AddResponse.class);
                addResponse.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(addResponse);
            }
        });

        titleTExt = (TextView) findViewById(R.id.titleText);
        dateText = (TextView) findViewById(R.id.dateText);

        only_pro = (RelativeLayout) findViewById(R.id.onlypro);
        onlyver = (RelativeLayout) findViewById(R.id.onlyver);

        mPicasso = Picasso.with(avatar.getContext());

        ImageView backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectActivity.this.finish();
            }
        });

        if (loadtarget == null) {
            loadtarget = new com.squareup.picasso.Target()  {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap
                    try{
                        avatar.setImageBitmap(roundImage(bitmap));
                    }catch (Exception e){
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
        loadProject();
    }

    public void loadProject(){
        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("id", Config.project_id);
        localRequestParams.put("method", "projects_get");
        NetManager.getInstance(this).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);
                    JSONObject localJSONObject1 = new JSONObject(str);

                    if(!localJSONObject1.get("error").toString().equals("0")){
                        Intent splash = new Intent(ProjectActivity.this,SplashScreenActivity.class);
                        splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(splash);
                        finish();
                    }

                    JSONObject localJSONObject2 = localJSONObject1.getJSONObject("data").getJSONObject("item");

                    project = new Project(localJSONObject2);

                    String price="По договоренности";
                    budgetText.setText("Бюджет: "+price);
                    try {
                        if (!project.currency.equals("0")) {
                            price = project.budget + " " + currency[Integer.parseInt(project.currency)] + dimension[Integer.parseInt(project.dimension)];
                        }

                        budgetText.setText("Бюджет: " + price);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    AvatarDrawable avatarDrawable = null;
                    avatarDrawable = new AvatarDrawable(project.user);
                    avatar.setImageDrawable(avatarDrawable);
                    if(!project.user.avatar.get("file").equals("")){
                        mPicasso.load(project.user.avatar.get("url")+"f_"+project.user.avatar.get("file")).into(loadtarget);
                    }

                    String username = "";

                    username = project.user.firstname;
                    if(!project.user.lastname.equals("")){
                        //userName.setText(project.user.firstname+" "+project.user.lastname);
                        username = project.user.firstname+" "+project.user.lastname;
                    }
                    if(!project.user.username.equals("")){
                        //userName.setText(project.user.firstname+" "+project.user.lastname+" ("+project.user.username+")");
                        username = project.user.firstname+" "+project.user.lastname+" ("+project.user.username+")";
                    }

                    userName.setText(username.trim());
                    if(project.user.online==1){
                        onlineStatus.setText("На сайте");
                    }else{
                        onlineStatus.setText("Нет на сайте");
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                    long timestamp = (long)project.create_time * 1000;
                    java.util.Date netDate = (new java.util.Date(timestamp));

                    dateText.setText(sdf.format(netDate));
                    titleTExt.setText(Html.fromHtml(project.title).toString());
                    descrText.setText(Html.fromHtml(project.descr).toString());

                    if(project.only_pro==1){
                        only_pro.setVisibility(View.VISIBLE);
                    }

                    if(project.only_verified==1){
                        onlyver.setVisibility(View.VISIBLE);
                    }

                    boolean select = false;

                    BusProvider.getInstance().post(new UpdateResponsesEvent(project.responses));

                    int myId = -1;

                    for(int i=0;i<project.responses.size();i++){
                        Responses resp = project.responses.get(i);

                        if(resp.user.id.equals(Config.myUser.id)){
                            myId = i;
                        }

                        if(resp.select==3){
                            select = true;
                            break;
                        }
                    }
                    responsesText.setText("ответов "+project.responses.size());
                    if(select){
                        responsesText.setText("исполнитель определен");
                        responsesText.setTextColor(Color.YELLOW);
                    }

                    responsesText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ResponsesActivity.responses = project.responses;
                            Intent showResp = new Intent(ProjectActivity.this,ResponsesActivity.class);
                            showResp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(showResp);
                        }
                    });

                    if(project.attaches.size()>0) {
                        FilesAdapter adapter = new FilesAdapter(attachesList.getContext(), project.attaches);
                        attachesList.setAdapter(adapter);
                        attachesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(project.attaches.get(position)));
                                startActivity(intent);
                            }
                        });
                    }else{
                        attachesList.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                finish();
                // new MessagesDialog(ProjectActivity.this, "Проект", "Во время загрузки проекта произошла ошибка соединения.\nПроверьте соединение и повторите попытку.").show();
            }
        });
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
        Appodeal.hide(this, Appodeal.BANNER_VIEW);
    }
}

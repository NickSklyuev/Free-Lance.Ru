package trilodi.ru.free_lanceru.UI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import trilodi.ru.free_lanceru.Components.AvatarDrawable;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.Project;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

public class ProjectActivity extends ActionBarActivity {

    Project project;
    ImageView avatar;
    TextView userName, onlineStatus;

    Picasso mPicasso;
    private com.squareup.picasso.Target loadtarget;

    AlertDialog dialog;
    View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

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

        mPicasso = Picasso.with(avatar.getContext());

        if (loadtarget == null) {
            loadtarget = new com.squareup.picasso.Target()  {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap
                    avatar.setImageBitmap(roundImage(bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable){

                }
            };
        }

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
                    JSONObject localJSONObject2 = localJSONObject1.getJSONObject("data").getJSONObject("item");

                    project = new Project(localJSONObject2);

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
               // new MessagesDialog(ProjectActivity.this, "Проект", "Во время загрузки проекта произошла ошибка соединения.\nПроверьте соединение и повторите попытку.").show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project, menu);
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
}

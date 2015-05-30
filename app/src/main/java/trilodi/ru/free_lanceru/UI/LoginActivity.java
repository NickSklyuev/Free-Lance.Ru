package trilodi.ru.free_lanceru.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.User;
import trilodi.ru.free_lanceru.Models.UserReview;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

/*hello world*/

public class LoginActivity extends ActionBarActivity {

    private ImageView acceptButton;
    private EditText loginEdit, passwordEdit;
    private TextView errorText;

    AlertDialog dialog;
    View loadingView;

    InputMethodManager imm;

    SharedPreferences localEditor;

    Button regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);

        localEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        regButton = (Button) findViewById(R.id.sendButton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regView = new Intent(LoginActivity.this, RegistrationActivity.class);
                regView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(regView);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);



        acceptButton = (ImageView) toolbar.findViewById(R.id.OK);

        errorText = (TextView) findViewById(R.id.errorText);
        errorText.setVisibility(View.GONE);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        loadingView = getLayoutInflater().inflate(R.layout.load_dialog_layout, null);
        ProgressBarCircularIndeterminate progresser = (ProgressBarCircularIndeterminate) loadingView.findViewById(R.id.dialogProgress);

        TextView dialogTitle = (TextView) loadingView.findViewById(R.id.dialogtitle);
        TextView dialogDescription = (TextView) loadingView.findViewById(R.id.dialogDescription);

        dialogTitle.setText(getResources().getString(R.string.AUTH_DIALOG_TITLE));
        dialogDescription.setText(getResources().getString(R.string.AUTH_DIALOG_TEXT));

        dialog = new AlertDialog.Builder(this).setView(loadingView).setCancelable(false).create();



        //acceptButton = (ImageView) findViewById(R.)

        loginEdit = (EditText) findViewById(R.id.loginEditText);
        passwordEdit = (EditText) findViewById(R.id.passwordEditText);

        loginEdit.setText(localEditor.getString("login",""));
        passwordEdit.setText(localEditor.getString("password",""));

        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    login();
                    return true;
                }
                return false;
            }
        });



    }

    private void login(){
        try{
            dialog.show();
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            imm.hideSoftInputFromWindow(loginEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            imm.hideSoftInputFromWindow(passwordEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){
            e.printStackTrace();
        }

        String login = loginEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
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
                        errorText.setVisibility(View.GONE);
                        Config.myUser = new User(response.getJSONObject("data"));

                        SharedPreferences.Editor localEditor2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        localEditor2.putString("login",Config.myUser.username);
                        localEditor2.putString("password", passwordEdit.getText().toString());
                        localEditor2.putLong("login_time", (System.currentTimeMillis() / 1000L));
                        localEditor2.putString("id", Config.myUser.id);
                        localEditor2.commit();

                        userGet();
                    }
                    if (response.get("error_text").toString().equals("ERROR_EMPTY_USERNAME")) {
                        errorText.setText(getResources().getString(R.string.ERROR_EMPTY_USERNAME));
                        errorText.setVisibility(View.VISIBLE);
                    }
                    if (response.get("error_text").toString().equals("ERROR_INVALID_PASSWORD")) {

                        errorText.setText(getResources().getString(R.string.ERROR_INVALID_PASSWORD));
                        errorText.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    errorText.setText(getResources().getString(R.string.ERROR_AUTH_EXCEPTION));
                    errorText.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }


            }

            @Override
            public void onFinish() {
                try{
                    dialog.dismiss();
                }catch(Exception e){
                    e.printStackTrace();
                }
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                try {
                    String str = new String(responseBody, "UTF-8");
                    errorText.setText(str);
                    errorText.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    // ERROR_TEXT.setText("Ошибка авторизации. Проверьте данные и повторите попытку");
                    errorText.setText(getResources().getString(R.string.ERROR_AUTH_EXCEPTION));
                    errorText.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        });
    }


    public void userGet(){
        dialog.show();
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
                        Config.myUser.rating = resp.getJSONObject("data").getJSONObject("user").getInt("rating");

                        for(int i=0; i<resp.getJSONObject("data").getJSONObject("user").getJSONArray("reviews").length();i++){
                            JSONObject review = resp.getJSONObject("data").getJSONObject("user").getJSONArray("reviews").getJSONObject(i);
                            Config.myUser.reviews.add(new UserReview(review));
                        }
                        if(localEditor.getBoolean("first_launch_not_login", true)){
                            Intent mainActivity = new Intent(LoginActivity.this, SplashScreenActivity.class);
                            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mainActivity);
                            finish();
                        }else{
                            Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mainActivity);
                            finish();
                        }
                    }

                    if (resp.get("error_text").toString().equals("ERROR_EMPTY_USERNAME")) {
                        errorText.setText(getResources().getString(R.string.ERROR_EMPTY_USERNAME));
                        errorText.setVisibility(View.VISIBLE);
                    }
                    if (resp.get("error_text").toString().equals("ERROR_INVALID_PASSWORD")) {

                        errorText.setText(getResources().getString(R.string.ERROR_INVALID_PASSWORD));
                        errorText.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    errorText.setText(getResources().getString(R.string.ERROR_AUTH_EXCEPTION));
                    errorText.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String str = new String(responseBody, "UTF-8");
                    errorText.setText(str);
                    errorText.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    // ERROR_TEXT.setText("Ошибка авторизации. Проверьте данные и повторите попытку");
                    errorText.setText(getResources().getString(R.string.ERROR_AUTH_EXCEPTION));
                    errorText.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        });
    }
}

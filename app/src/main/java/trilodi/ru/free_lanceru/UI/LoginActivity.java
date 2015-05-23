package trilodi.ru.free_lanceru.UI;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.User;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

/*hello world*/

public class LoginActivity extends ActionBarActivity {

    private ImageView acceptButton;
    private EditText loginEdit, passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);

        acceptButton = (ImageView) toolbar.findViewById(R.id.OK);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        //acceptButton = (ImageView) findViewById(R.)

        loginEdit = (EditText) findViewById(R.id.loginEditText);
        passwordEdit = (EditText) findViewById(R.id.passwordEditText);

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
        String login = loginEdit.getText().toString();
        String password = passwordEdit.getText().toString();
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
                    System.out.println(str);
                    JSONObject response = new JSONObject(str);
                    if (Integer.parseInt(response.get("error").toString()) == 0) {
                        Config.myUser = new User(response.getJSONObject("data"));


                        System.out.println(Config.myUser);
                        System.out.println(Config.myUser.firstname);
                    }
                    if (response.get("error_text").toString().equals("ERROR_EMPTY_USERNAME")) {
                        //new MessagesDialog(LoginActivity.this, "Авторизация", "Логин не может быть пустым.").show();
                        //ERROR_TEXT.setText("Логин не может быть пустым");
                    }
                    if (response.get("error_text").toString().equals("ERROR_INVALID_PASSWORD")) {
                        //new MessagesDialog(LoginActivity.this, "Авторизация", "Неправильный пароль.").show();
                        //ERROR_TEXT.setText("Неправильный пароль");
                    }

                } catch (Exception e) {
                    //new MessagesDialog(LoginActivity.this, "Авторизация", "Во время авторизации было утеряно соединение с сервером.\nПроверьте соединение и повторите попытку.").show();
                    //ERROR_TEXT.setText("Ошибка авторизации. Проверьте данные и повторите попытку");
                    e.printStackTrace();
                }


            }

            @Override
            public void onFinish() {

                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                try {
                    // progDailog.dismiss();
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);
                } catch (Exception e) {
                    // ERROR_TEXT.setText("Ошибка авторизации. Проверьте данные и повторите попытку");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}

package trilodi.ru.free_lance.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import trilodi.ru.free_lance.Config;
import trilodi.ru.free_lance.Network.NetManager;

public class MainService extends Service {

    String SENDER_ID = "81567517645";

    static final String TAG = "GCMDemo";

    public static GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;


    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        Intent service =new Intent(this,MainService.class);
        service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(service);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        context = getApplicationContext();

        gcm = GoogleCloudMessaging.getInstance(context);

        try {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Config.REG_ID = gcm.register(SENDER_ID);
                        System.out.println(Config.REG_ID);



                    } catch (Exception bug) {
                        bug.printStackTrace();
                    }

                }
            });

            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("method", "device_register");
        localRequestParams.put("device_id", Config.REG_ID);
        NetManager.getInstance(getApplicationContext()).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);
                    JSONObject response = new JSONObject(str);

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
                // progDailog.dismiss();
            }
        });


        return Service.START_STICKY;
    }

}

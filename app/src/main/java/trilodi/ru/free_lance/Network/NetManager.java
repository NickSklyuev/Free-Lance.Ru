package trilodi.ru.free_lance.Network;

import android.app.AlertDialog;
import android.content.Context;
import android.provider.Settings;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lance.Config;

/**
 * Created by REstoreService on 23.05.15.
 */
public class NetManager {
    public static String SERVER_URL = "https://www.fl.ru";
    public static String BASE_URL = SERVER_URL + "/external/post-json/index.php";
    //public static CookieStore COOKIE_STORE = new BasicCookieStore();


    private static AsyncHttpClient asyncHttpClient;
    private static Context context;
    private final static NetManager singletone = new NetManager();

    private static PersistentCookieStore mCookieStore;


    private static String getAbsoluteUrl(String paramString)
    {
        return BASE_URL + paramString;
    }

    public static NetManager getInstance(Context paramContext)
    {
        context = paramContext;

        asyncHttpClient = new AsyncHttpClient(true, 80, 443);
        asyncHttpClient.setUserAgent("Mozilla/5.0(Linux; Android) AppleWebKit/533.0 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        asyncHttpClient.setTimeout(15000);
        mCookieStore = new PersistentCookieStore(paramContext);
        asyncHttpClient.setCookieStore(mCookieStore);
        asyncHttpClient.setBasicAuth("freelance", "mRfLjovLToupZM0");
        return singletone;
    }


    public static boolean isOnline()
    {
        //NetworkInfo localNetworkInfo = ((ConnectivityManager)mContext.getSystemService("connectivity")).getActiveNetworkInfo();
        //return (localNetworkInfo != null) && (localNetworkInfo.isConnectedOrConnecting());
        return true;
    }

    public void get(String paramString, RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler)
    {
        if (isOnline())
        {
            asyncHttpClient.get(getAbsoluteUrl(paramString), paramRequestParams, paramAsyncHttpResponseHandler);
            return;
        }
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle(2131165333);
        localBuilder.setMessage(2131165335);
        localBuilder.show();
    }

    public void post(RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler)
    {
        if (isOnline())
        {
            Config.context= FreeLanceApplication.getContext();
            Config.DEVICE_ID = Settings.Secure.getString(Config.context.getContentResolver(), "android_id");
            Config.persistentCookieStore = new PersistentCookieStore(Config.context);
            paramRequestParams.put("api", "mobile");
            paramRequestParams.put("udid", Config.DEVICE_ID);
            paramRequestParams.put("agent", "android");
            String str = BASE_URL;
            asyncHttpClient.post(BASE_URL, paramRequestParams, paramAsyncHttpResponseHandler);
        }
    }

    public void rndnum(RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler)
    {
        if (isOnline())
        {
            asyncHttpClient.get("https://www.fl.ru/image.php", paramRequestParams, paramAsyncHttpResponseHandler);
        }
    }


}

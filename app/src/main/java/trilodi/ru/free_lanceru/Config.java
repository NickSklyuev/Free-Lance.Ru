package trilodi.ru.free_lanceru;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import trilodi.ru.free_lanceru.Components.DBOpenHelper;
import trilodi.ru.free_lanceru.Components.ImagesCache;
import trilodi.ru.free_lanceru.Models.User;

/**
 * Created by REstoreService on 23.05.15.
 */
public class Config {
    public static Context context;
    public static PersistentCookieStore persistentCookieStore;

    public static String DEVICE_ID;
    public static String REG_ID;
    public static String project_id;


    public static CookieStore COOKIE_STORE = new BasicCookieStore();
    public static ImagesCache cache = ImagesCache.getInstance();

    public static DBOpenHelper dbHelper;
    public static SQLiteDatabase db;

    public static User myUser = null;

    public static String appKey = "b1f815106266f11a9368fd47e8b666bdb1fae1913354f864";

    static {
        dbHelper=new DBOpenHelper(FreeLanceApplication.getContext());
        db = dbHelper.getWritableDatabase();
    }


    public static long getTimestampFromDate(java.util.Date paramDate)
    {
        return paramDate.getTime();
    }


    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
        }
    }
}

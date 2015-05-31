package trilodi.ru.free_lance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import trilodi.ru.free_lance.Components.ImagesCache;
import trilodi.ru.free_lance.Models.User;
import trilodi.ru.free_lance.Components.DBOpenHelper;

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

    public static String appKey = "f5ae1df68dc2a35daf312a4de0051f34862c5d9507a0a143";

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

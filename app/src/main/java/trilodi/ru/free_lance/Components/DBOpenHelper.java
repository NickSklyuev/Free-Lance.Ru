package trilodi.ru.free_lance.Components;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by REstoreService on 19.12.14.
 */
public class DBOpenHelper extends SQLiteOpenHelper
{
    private static String DB_NAME = "freelance.sqlite";
    File sdcard = Environment.getExternalStorageDirectory() ;
    private static String DB_PATH = "/data/data/trilodi.ru.free_lance/databases/";
    private final Context mContext;
    private SQLiteDatabase mDB;
    File folder;

    public DBOpenHelper(Context paramContext)
    {
        super(paramContext, DB_NAME, null, 1);

        folder = new File(sdcard.getAbsoluteFile(), ".freelance_files");
        if(!folder.exists()){
            folder.mkdir();
        }

        this.mContext = paramContext;
        boolean dbexist = checkdatabase();
        try{
            if (dbexist) {
                //System.out.println("Database exists");
                opendatabase();
            } else {
                System.out.println("Database doesn't exist");
                createdatabase();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkDataBase()
    {
        System.out.println(folder.getAbsoluteFile() +"/" + DB_NAME);
        try
        {
            SQLiteDatabase localSQLiteDatabase1 = null;
            SQLiteDatabase localSQLiteDatabase2 = SQLiteDatabase.openDatabase(folder.getAbsoluteFile() +"/" + DB_NAME, null, 16);
            localSQLiteDatabase1 = localSQLiteDatabase2;
            if (localSQLiteDatabase1 != null)
                localSQLiteDatabase1.close();
            return localSQLiteDatabase1 != null;
        }
        catch (SQLiteException localSQLiteException)
        {
            localSQLiteException.printStackTrace();
            return false;
        }
    }

    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if(dbexist) {
            //System.out.println(" Database exists.");
        } else {
            this.getReadableDatabase();
            try {
                copydatabase();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkdatabase() {
        //SQLiteDatabase checkdb = null;
        boolean checkdb = false;
        try {
            String myPath = folder.getAbsoluteFile() +"/" + DB_NAME;
            File dbfile = new File(myPath);
            //checkdb = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
            checkdb = dbfile.exists();
        } catch(SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = mContext.getAssets().open("freelance.sqlite");

        // Path to the just created empty db
        String outfilename = folder.getAbsoluteFile() +"/" + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream(outfilename);

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer))>0) {
            myoutput.write(buffer,0,length);
        }

        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    public void opendatabase() {
        //Open the database
        String mypath = folder.getAbsoluteFile() +"/" + DB_NAME;
        mDB = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if(mDB != null) {
            mDB.close();
        }
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
        Log.d("DBHelper", "--- onCreate database ---");
        // создаем таблицу с полями
        try {
            paramSQLiteDatabase.execSQL("CREATE TABLE \"message\" (\n" +
                    "\t \"id\" integer,\n" +
                    "\t \"create_time\" integer,\n" +
                    "\t \"update_time\" integer,\n" +
                    "\t \"from_id\" integer,\n" +
                    "\t \"to_id\" integer,\n" +
                    "\t \"text\" text,\n" +
                    "\t \"status\" integer,\n" +
                    "\t \"read\" integer\n" +
                    ");");
            paramSQLiteDatabase.execSQL("CREATE TABLE \"user\" (\n" +
                    "\t \"id\" integer,\n" +
                    "\t \"create_time\" integer,\n" +
                    "\t \"update_time\" integer,\n" +
                    "\t \"status\" integer,\n" +
                    "\t \"username\" text,\n" +
                    "\t \"email\" text,\n" +
                    "\t \"firstname\" text,\n" +
                    "\t \"lastname\" text,\n" +
                    "\t \"role\" integer,\n" +
                    "\t \"pro\" integer,\n" +
                    "\t \"verified\" integer,\n" +
                    "\t \"spec\" integer,\n" +
                    "\t \"avatar_url\" text\n" +
                    ");");
            paramSQLiteDatabase.execSQL("CREATE TABLE \"category\" (\n" +
                    "\t \"id\" integer,\n" +
                    "\t \"category_group_id\" integer,\n" +
                    "\t \"sequence\" integer,\n" +
                    "\t \"title\" text,\n" +
                    "\t \"status\" integer,\n" +
                    "\t \"create_time\" integer,\n" +
                    "\t \"update_time\" integer\n" +
                    ");");
            paramSQLiteDatabase.execSQL("CREATE TABLE \"category_group\" (\n" +
                    "\t \"id\" integer,\n" +
                    "\t \"sequence\" integer,\n" +
                    "\t \"title\" text,\n" +
                    "\t \"status\" integer,\n" +
                    "\t \"create_time\" integer,\n" +
                    "\t \"update_time\" integer\n" +
                    ");");
            paramSQLiteDatabase.execSQL("CREATE TABLE \"city\" (\n" +
                    "\t \"id\" integer,\n" +
                    "\t \"sequence\" integer,\n" +
                    "\t \"title\" text,\n" +
                    "\t \"status\" integer,\n" +
                    "\t \"country_id\" integer,\n" +
                    "\t \"create_time\" integer,\n" +
                    "\t \"update_time\" integer\n" +
                    ");");
            paramSQLiteDatabase.execSQL("CREATE TABLE \"country\" (\n" +
                    "\t \"id\" integer,\n" +
                    "\t \"sequence\" integer,\n" +
                    "\t \"title\" text,\n" +
                    "\t \"status\" integer,\n" +
                    "\t \"create_time\" integer,\n" +
                    "\t \"update_time\" integer\n" +
                    ");");
            paramSQLiteDatabase.execSQL("CREATE TABLE \"filter\" (\n" +
                    "\t \"id\" integer,\n" +
                    "\t \"enabled\" integer,\n" +
                    "\t \"keyword\" text\n" +
                    ");");
            paramSQLiteDatabase.execSQL("CREATE TABLE \"filter_item\" (\n" +
                    "\t \"id\" integer,\n" +
                    "\t \"filter_id\" integer,\n" +
                    "\t \"category_group_id\" integer,\n" +
                    "\t \"category_id\" integer\n" +
                    ");");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
    }


}
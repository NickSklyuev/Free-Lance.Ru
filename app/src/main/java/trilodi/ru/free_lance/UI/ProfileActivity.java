package trilodi.ru.free_lance.UI;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import trilodi.ru.free_lance.Components.DBOpenHelper;
import trilodi.ru.free_lance.Config;
import trilodi.ru.free_lanceru.R;

public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Config.dbHelper=new DBOpenHelper(this);
        Config.db = Config.dbHelper.getWritableDatabase();
    }



}

package trilodi.ru.free_lanceru.Models;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by REstoreService on 28.05.15.
 */
public class FavoriteUser extends User{
    public int pro = 0;
    public String lastname = "";
    public int status =1;
    public int spec = 0;
    public int city_id = 0;
    public String firstname = "";
    public String birthday = "";
    public int prof_id = 0;
    public int create_time = 1299790800;
    public int verified = 0;
    public int prof_group_id = 0;
    public String id = "0";
    public int gender = 0;
    public int country_id = 0;
    public int role = 2;
    public int age = 29;
    public int update_time = 0;
    public int online = 0;
    public String username = "";
    public Map<String, String> avatar = new HashMap<String, String>();

    public FavoriteUser(JSONObject favorites_list){
        try{
            this.id = favorites_list.get("id").toString();
            this.pro = favorites_list.getInt("pro");
            this.verified = favorites_list.getInt("verified");
            this.role = favorites_list.getInt("role");
            this.status = favorites_list.getInt("status");
            this.gender = favorites_list.getInt("gender");
            this.city_id = favorites_list.getInt("city_id");
            this.country_id = favorites_list.getInt("country_id");
            this.prof_id = favorites_list.getInt("prof_id");
            this.prof_group_id = favorites_list.getInt("prof_group_id");
            this.firstname = favorites_list.getString("firstname");
            this.lastname = favorites_list.getString("lastname");
            this.birthday = favorites_list.getString("birthday");
            this.username = favorites_list.getString("username");
            this.create_time = favorites_list.getInt("create_time");
            this.update_time = favorites_list.getInt("update_time");

            JSONObject avatar_data = favorites_list.getJSONObject("avatar");
            this.avatar.put("url", avatar_data.getString("url"));
            this.avatar.put("file", avatar_data.getString("file"));

            this.age = favorites_list.getInt("age");
            this.online = favorites_list.getInt("online");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

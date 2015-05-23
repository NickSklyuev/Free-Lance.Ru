package trilodi.ru.free_lanceru.Models;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by REstoreService on 23.05.15.
 */
public class User {

    private JSONObject userData;

    public String id;
    public int pro;
    public int verified;
    public int role;
    public int status;
    public String spec;
    public int gender;
    public int city_id;
    public int country_id;
    public String prof_id;
    public String prof_group_id;
    public String firstname;
    public String lastname;
    public String birthday;
    public String username;
    public String email;
    public int create_time;
    public int update_time;
    public Map<String, String> avatar = new HashMap<String, String>();
    public int age;
    public int online;

    public User(JSONObject userData){
        this.userData = userData;
        try{
            this.id = userData.get("id").toString();
            this.pro = userData.getInt("pro");
            this.verified = userData.getInt("verified");
            this.role = userData.getInt("role");
            this.status = userData.getInt("status");
            this.spec = userData.getString("spec");
            this.gender = userData.getInt("gender");
            this.city_id = userData.getInt("city_id");
            this.country_id = userData.getInt("country_id");
            this.prof_id = userData.getString("prof_id");
            this.prof_group_id = userData.getString("prof_group_id");
            this.firstname = userData.getString("firstname");
            this.lastname = userData.getString("lastname");
            this.birthday = userData.getString("birthday");
            this.username = userData.getString("username");
            this.email = userData.getString("email");
            this.create_time = userData.getInt("create_time");
            this.update_time = userData.getInt("update_time");

            JSONObject avatar_data = userData.getJSONObject("avatar");
            this.avatar.put("url", avatar_data.getString("url"));
            this.avatar.put("file", avatar_data.getString("file"));

            this.age = userData.getInt("age");
            this.online = userData.getInt("online");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
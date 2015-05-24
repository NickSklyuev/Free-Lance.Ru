package trilodi.ru.free_lanceru.Models;

import org.json.JSONObject;

/**
 * Created by REstoreService on 24.05.15.
 */
public class Project {
    public String id;
    public User user;
    public int create_time;
    public int update_time;
    public int only_pro;
    public int only_verified;
    public String country_id;
    public String category_id;
    public String descr;
    public String user_id;
    public String title;
    public String city_id;
    public String budget;
    public String dimension;
    public String subcategory_id;
    public String kind;
    public int budget_agreement;
    public String currency;
    public int status;

    public Project(JSONObject projectJson){
        try{
            this.id = projectJson.getString("id");
            this.user = new User(projectJson.getJSONObject("user"));
            this.create_time = projectJson.getInt("create_time");
            this.update_time = projectJson.getInt("update_time");
            this.only_pro = projectJson.getInt("only_pro");
            this.only_verified = projectJson.getInt("only_verified");
            this.country_id = projectJson.getString("country_id");
            this.category_id = projectJson.getString("category_id");
            this.descr = projectJson.getString("descr");
            this.user_id = projectJson.getString("user_id");
            this.title = projectJson.getString("title");
            this.city_id = projectJson.getString("city_id");
            this.budget = projectJson.getString("budget");
            this.dimension = projectJson.getString("dimension");
            this.subcategory_id = projectJson.getString("subcategory_id");
            this.kind = projectJson.getString("kind");
            this.budget_agreement = projectJson.getInt("budget_agreement");
            this.currency = projectJson.getString("currency");
            this.status = projectJson.getInt("status");
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}

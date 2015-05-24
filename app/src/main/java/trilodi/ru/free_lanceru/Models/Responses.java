package trilodi.ru.free_lanceru.Models;

import org.json.JSONObject;

/**
 * Created by REstoreService on 25.05.15.
 */
public class Responses {
    public String id;
    public int select;
    public User user;
    public int create_time;
    public int update_time;
    public String comment;
    public int only_customer;
    public String user_id;
    public String term_dimension;
    public String budget;
    public String project_id;
    public String term;
    public String currency;
    public int status;
    public Responses(JSONObject responses){
        try{
            this.id = responses.getString("id");
            this.user = new User(responses.getJSONObject("user"));
            this.comment = responses.getString("comment");
            this.create_time = responses.getInt("create_time");
            this.select  =responses.getInt("select");
            this.update_time = responses.getInt("update_time");
            this.only_customer = responses.getInt("only_customer");
            this.user_id  =responses.getString("user_id");
            this.term_dimension = responses.getString("term_dimension");
            this.budget = responses.getString("budget");
            this.project_id = responses.getString("project_id");
            this.term = responses.getString("term");
            this.currency = responses.getString("currency");
            this.status = responses.getInt("status");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

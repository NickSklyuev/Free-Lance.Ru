package trilodi.ru.free_lance.Models;

import org.json.JSONObject;

/**
 * Created by REstoreService on 28.05.15.
 */
public class UserReview {
    public int status = 0;
    public int update_time;
    public int create_time;
    public String rate;
    public String id;
    public String from_user_id;
    public String to_user_id;
    public String text;
    public int type;
    public User user;

    public UserReview(JSONObject review){
        try{
            this.status = review.getInt("status");
            this.update_time = review.getInt("update_time");
            this.create_time = review.getInt("create_time");
            this.rate = review.getString("rate");
            this.id = review.getString("id");
            this.from_user_id = review.getString("from_user_id");
            this.to_user_id = review.getString("to_user_id");
            this.text = review.getString("text");
            this.type = review.getInt("type");
            this.user = new User(review.getJSONObject("user"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

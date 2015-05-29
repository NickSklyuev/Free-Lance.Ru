package trilodi.ru.free_lanceru.Models;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by REstoreService on 29.05.15.
 */
public class Portfolio {
    public int status;
    public int update_time;
    public String sequence;
    public String id;
    public String category_group_id;
    public String category_id;
    public String title;
    public Map<String, String> image = new HashMap<String, String>();
    public String user_id;
    public int create_time = 0;

    public Portfolio(JSONObject portfolio){
        try{
            this.status = portfolio.getInt("status");
            this.update_time = portfolio.getInt("update_time");
            this.sequence = portfolio.getString("sequence");
            this.id = portfolio.getString("id");
            this.category_group_id = portfolio.getString("category_group_id");
            this.category_id = portfolio.getString("category_id");
            this.title = portfolio.getString("title");

            JSONObject image = portfolio.getJSONObject("image");
            this.image.put("url", image.getString("url"));
            this.image.put("file", image.getString("file"));

            this.user_id = portfolio.getString("user_id");
            //this.create_time = Integer.parseInt(portfolio.get("create_time").toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

package trilodi.ru.free_lanceru.Models;

import org.json.JSONObject;

/**
 * Created by REstoreService on 26.05.15.
 */
public class Messages {
    public String id;
    public String create_time;
    public String update_time;
    public String from_id;
    public String to_id;
    public String text;
    public String status;
    public String read;

    public Messages(JSONObject message){
        try{
            this.id             = message.get("id").toString();
            this.create_time    = message.get("create_time").toString();
            this.update_time    = message.get("update_time").toString();
            this.from_id        = message.get("from_id").toString();
            this.to_id          = message.get("to_id").toString();
            this.text           = message.get("text").toString();
            this.status         = message.get("status").toString();
            this.read           = message.get("read").toString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Messages(){}
}

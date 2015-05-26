package trilodi.ru.free_lanceru.Models;

/**
 * Created by REstoreService on 27.05.15.
 */
public class Chats {
    public String from_id;
    public String to_id;
    public int messages = 0;
    public int unreded = 0;
    public String id;
    public User user;

    public Chats(String from_id, String to_id, int messages, int unreded, String id, User user){
        this.from_id = from_id;
        this.to_id = to_id;
        this.messages = messages;
        this.unreded = unreded;
        this.id = id;
        this.user = user;
    }
}

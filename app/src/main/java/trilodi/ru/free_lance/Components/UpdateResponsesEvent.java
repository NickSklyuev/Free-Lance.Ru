package trilodi.ru.free_lance.Components;

import java.util.ArrayList;

import trilodi.ru.free_lance.Models.Responses;

/**
 * Created by REstoreService on 25.05.15.
 */
public class UpdateResponsesEvent {
    public ArrayList<Responses> responses = new ArrayList<Responses>();

    public UpdateResponsesEvent(ArrayList<Responses> responses){
        this.responses = responses;
    }

}

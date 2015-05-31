package trilodi.ru.free_lance.Models;

/**
 * Created by REstoreService on 30.05.15.
 */
public class Categories {

    private String catId;
    private String catTitle;

    public Categories(String catId, String catTitle) {
        super();

        this.catId=catId;
        this.catTitle=catTitle;
    }

    public String getCatId(){
        return this.catId;
    }

    public String getCatTitle(){
        return this.catTitle;
    }


}

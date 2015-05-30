package trilodi.ru.free_lanceru.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import trilodi.ru.free_lanceru.Models.Categories;
import trilodi.ru.free_lanceru.R;

/**
 * Created by REstoreService on 17.12.14.
 */
public class CategoriesAdapter extends ArrayAdapter<Categories> {

    private final Context context;
    private final ArrayList<Categories> itemsArrayList;

    public CategoriesAdapter(Context context, ArrayList<Categories> itemsArrayList) {

        super(context, R.layout.cat_list_item, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.cat_list_item, parent, false);

        TextView catName = (TextView)rowView.findViewById(R.id.textView26);

        catName.setText(itemsArrayList.get(position).getCatTitle());

        return rowView;
    }
}


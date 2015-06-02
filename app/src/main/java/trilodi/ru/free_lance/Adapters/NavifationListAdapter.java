package trilodi.ru.free_lance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.squareup.picasso.Picasso;

import trilodi.ru.free_lanceru.R;

/**
 * Created by REstoreService on 30.05.15.
 */
public class NavifationListAdapter extends BaseAdapter {
    private Context context;

    String[] cats = new String[4];

    Picasso mPicasso;
    private com.squareup.picasso.Target loadtarget;

    ImageView image;
    ProgressBarCircularIndeterminate progress;

    public NavifationListAdapter(Context c, String[] cats) {
        context = c;
        this.cats = cats;
    }

    public int getCount() {
        return 4;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.nav_list_ellement, parent, false);

        TextView li = (TextView) rowView.findViewById(R.id.tv);

        li.setText(cats[position]);

        switch (position){
            case 0:
                li.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_projects, 0, 0, 0);
                break;
            case 1:
                li.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message, 0, 0, 0);
                break;
            case 2:
                li.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorites, 0, 0, 0);
                break;
            case 3:
                li.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_profile, 0, 0, 0);
                break;
        }



        return rowView;
    }

}
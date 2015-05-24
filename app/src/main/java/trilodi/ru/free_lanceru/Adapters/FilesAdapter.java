package trilodi.ru.free_lanceru.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import trilodi.ru.free_lanceru.R;

/**
 * Created by REstoreService on 25.05.15.
 */
public class FilesAdapter extends ArrayAdapter<String> {

    private Context context;
    List<String> attaches = new ArrayList<String>();

    public FilesAdapter(Context context, List<String> attaches) {
        super(context, R.layout.files_ellement, attaches);
        this.attaches = attaches;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.files_ellement, parent, false);

        TextView fileName = (TextView) rowView.findViewById(R.id.fileName);

        String file = attaches.get(position);

        String[] split = file.split("/");

        fileName.setText(split[split.length-1]);

        return rowView;
    }


}

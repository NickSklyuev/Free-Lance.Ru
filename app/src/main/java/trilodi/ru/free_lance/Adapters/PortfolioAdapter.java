package trilodi.ru.free_lance.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import trilodi.ru.free_lance.Models.Portfolio;
import trilodi.ru.free_lanceru.R;

/**
 * Created by REstoreService on 30.05.15.
 */
public class PortfolioAdapter extends BaseAdapter {
    private Context context;

    ArrayList<Portfolio> portfolio = new ArrayList<Portfolio>();

    Picasso mPicasso;
    private com.squareup.picasso.Target loadtarget;

    ImageView image;
    ProgressBarCircularIndeterminate progress;

    public PortfolioAdapter(Context c, ArrayList<Portfolio> portfolio) {
        context = c;
        this.portfolio = portfolio;
    }

    public int getCount() {
        return this.portfolio.size();
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
        View rowView = inflater.inflate(R.layout.portfolio_grid_ellement, parent, false);

        Portfolio port = portfolio.get(position);

        image = (ImageView) rowView.findViewById(R.id.imageView8);
        progress = (ProgressBarCircularIndeterminate) rowView.findViewById(R.id.dialogProgress);

        mPicasso = Picasso.with(image.getContext());

        if (loadtarget == null) {
            loadtarget = new com.squareup.picasso.Target()  {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap

                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable){

                }
            };
        }

        if(!port.image.get("url").equals("")){
            mPicasso.load(port.image.get("url")+"sm_f_"+port.image.get("file")).resize(300, 300).centerCrop().into(image);
            progress.setVisibility(View.GONE);
        }

        return rowView;
    }

}
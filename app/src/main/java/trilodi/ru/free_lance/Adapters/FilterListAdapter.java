package trilodi.ru.free_lance.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import trilodi.ru.free_lance.Components.BusProvider;
import trilodi.ru.free_lance.Components.UpdateFilterEvent;
import trilodi.ru.free_lance.Models.Categories;
import trilodi.ru.free_lanceru.R;
import trilodi.ru.free_lance.UI.FilterActivity;

/**
 * Created by REstoreService on 24.05.15.
 */
public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {
    private ArrayList<Categories> itemsArrayList = new ArrayList<Categories>();

    OnItemClickListener mItemClickListener;


    public FilterListAdapter(ArrayList<Categories> itemsArrayList){
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public FilterListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_ellement, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FilterListAdapter.ViewHolder holder, int i) {
        Categories category = itemsArrayList.get(i);
        holder.title.setText(category.getCatTitle());
    }

    @Override
    public int getItemCount() {
        return this.itemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        ImageView deleter;
        TextView title;
        TextView mText;
        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.textView21);
            deleter = (ImageView) v.findViewById(R.id.imageView9);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View dcView = v.inflate(v.getContext(),R.layout.dialog_message, null);
                    final AlertDialog deleteCategory = new AlertDialog.Builder(v.getContext()).setView(dcView).setCancelable(true).create();
                    mText=(TextView)dcView.findViewById(R.id.textView27);

                    mText.setText("Вы действительно хотите удалить категорию \"" + FilterActivity.categoriesMainList.get(getPosition()).getCatTitle() + "\"?");
                    deleteCategory.show();

                    Button ok=(Button)dcView.findViewById(R.id.okay);
                    Button cancel=(Button)dcView.findViewById(R.id.cancel);

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int cids = getPosition();

                            BusProvider.getInstance().post(new UpdateFilterEvent(cids));
                            deleteCategory.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteCategory.dismiss();
                        }
                    });
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition()); //OnItemClickListener mItemClickListener;
            }

        }


    }

    public Bitmap roundImage(Bitmap bm){
        //java.io.File image = new java.io.File(path);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        Bitmap bitmap = bm;

        Bitmap bitmapRounded = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(bitmapRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight())), 80, 80, paint);

        return bitmapRounded;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

}

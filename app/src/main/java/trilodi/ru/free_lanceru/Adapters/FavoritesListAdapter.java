package trilodi.ru.free_lanceru.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import trilodi.ru.free_lanceru.Components.AvatarDrawable;
import trilodi.ru.free_lanceru.Models.FavoriteUser;
import trilodi.ru.free_lanceru.R;
import trilodi.ru.free_lanceru.UI.ProfileActivity;
import trilodi.ru.free_lanceru.UI.ProfileFragment;

/**
 * Created by REstoreService on 24.05.15.
 */
public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.ViewHolder> {
    ArrayList<FavoriteUser> responses;

    Picasso mPicasso;
    private com.squareup.picasso.Target loadtarget;
    FavoritesListAdapter.ViewHolder h;

    public FavoritesListAdapter(ArrayList<FavoriteUser> responses){
        this.responses = responses;
    }

    @Override
    public FavoritesListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorites_list_ellement, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FavoritesListAdapter.ViewHolder holder, int i) {
        h = holder;

        FavoriteUser response = this.responses.get(i);

        mPicasso = Picasso.with(holder.avatarImage.getContext());

        AvatarDrawable avatarDrawable = null;
        avatarDrawable = new AvatarDrawable(response);
        holder.avatarImage.setImageDrawable(avatarDrawable);

        if (loadtarget == null) {
            loadtarget = new com.squareup.picasso.Target()  {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap
                    h.avatarImage.setImageBitmap(roundImage(bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable){

                }
            };
        }

        if(!response.avatar.get("file").equals("")){
            mPicasso.load(response.avatar.get("url")+"f_"+response.avatar.get("file")).into(loadtarget);
        }

        String username = "";

        username = response.firstname;
        if(!response.lastname.equals("")){
            //userName.setText(project.user.firstname+" "+project.user.lastname);
            username = response.firstname+" "+response.lastname;
        }
        if(!response.username.equals("")){
            //userName.setText(project.user.firstname+" "+project.user.lastname+" ("+project.user.username+")");
            username = response.firstname+" "+response.lastname+" ("+response.username+")";
        }

        holder.userName.setText(username.trim());
        if(response.online==1){
            holder.onlineStatus.setText("На сайте");
        }else{
            holder.onlineStatus.setText("Нет на сайте");
        }

        if(response.pro==1){
            holder.is_pro.setVisibility(View.VISIBLE);
        }else{
            holder.is_pro.setVisibility(View.GONE);
        }

        if(response.verified==1){
            holder.is_ver.setVisibility(View.VISIBLE);
        }else{
            holder.is_ver.setVisibility(View.GONE);
        }

        if(response.role == 2){
            holder.comment.setText("Работодатель");
        }
        if(response.role == 1){
            holder.comment.setText("Фрилансер");
        }


    }

    @Override
    public int getItemCount() {
        return this.responses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        CardView responseCard;
        TextView userName, onlineStatus,comment;
        ImageView is_pro, is_ver, avatarImage;
        public ViewHolder(View v) {
            super(v);

            responseCard = (CardView) v.findViewById(R.id.card_view);
            userName = (TextView)responseCard.findViewById(R.id.userName);
            onlineStatus = (TextView)responseCard.findViewById(R.id.online_status);
            comment = (TextView) responseCard.findViewById(R.id.commentText);

            is_pro = (ImageView) responseCard.findViewById(R.id.is_pro);
            is_ver = (ImageView) responseCard.findViewById(R.id.is_ver);

            avatarImage = (ImageView) responseCard.findViewById(R.id.avatarImage);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ProfileFragment.userId = responses.get(getPosition()).id;

                    Intent userProfile = new Intent(v.getContext(), ProfileActivity.class);
                    userProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(userProfile);
                }
            });

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

}

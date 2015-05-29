package trilodi.ru.free_lanceru.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import trilodi.ru.free_lanceru.Components.AvatarDrawable;
import trilodi.ru.free_lanceru.FreeLanceApplication;
import trilodi.ru.free_lanceru.Models.Chats;
import trilodi.ru.free_lanceru.Models.User;
import trilodi.ru.free_lanceru.R;
import trilodi.ru.free_lanceru.UI.MessageActivity;

/**
 * Created by REstoreService on 24.05.15.
 */
public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder> {
    ArrayList<Chats> chats;

    Picasso mPicasso;
    private com.squareup.picasso.Target loadtarget;
    ChatsListAdapter.ViewHolder h;

    public ChatsListAdapter(ArrayList<Chats> chats){
        this.chats = chats;
    }

    @Override
    public ChatsListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chats_list_ellement, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ChatsListAdapter.ViewHolder holder, int i) {
        Chats chat = this.chats.get(i);

        h = holder;

        User user = chat.user;

        mPicasso = Picasso.with(holder.avatar.getContext());

        AvatarDrawable avatarDrawable = null;
        avatarDrawable = new AvatarDrawable(user);
        holder.avatar.setImageDrawable(avatarDrawable);

        if (loadtarget == null) {
            loadtarget = new com.squareup.picasso.Target()  {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap

                    try{
                        h.avatar.setImageBitmap(roundImage(bitmap));
                        h.avatar.refreshDrawableState();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable){

                }
            };
        }

        if(!user.avatar_url.equals("")){
            mPicasso.load(user.avatar_url).into(loadtarget);
        }

        String username = "";

        username = user.firstname;
        if(!user.lastname.equals("")){
            //userName.setText(project.user.firstname+" "+project.user.lastname);
            username = user.firstname+" "+user.lastname;
        }
        if(!user.username.equals("")){
            //userName.setText(project.user.firstname+" "+project.user.lastname+" ("+project.user.username+")");
            username = user.firstname+" "+user.lastname+" ("+user.username+")";
        }

        holder.user_name.setText(username.trim());



        if(chat.unreded>0){
            String messages = "Сообщений " + chat.messages +" / ";
            String new_messages = "Новых " + chat.unreded;
            Spannable wordtoSpan = new SpannableString(messages+new_messages);
            wordtoSpan.setSpan(new ForegroundColorSpan(FreeLanceApplication.getContext().getResources().getColor(R.color.primary)), messages.length(), (messages.length())+new_messages.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), messages.length(), (messages.length())+new_messages.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.messages_count.setText(wordtoSpan);
        }else{
            holder.messages_count.setText("Сообщений " + chat.messages +" / Новых "+chat.unreded);
        }


        holder.verImage.setVisibility(View.GONE);
        holder.proImage.setVisibility(View.GONE);

        if(user.pro == 1){
            holder.proImage.setVisibility(View.VISIBLE);
        }

        if(user.verified == 1){
            holder.verImage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return this.chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case

        ImageView avatar, verImage, proImage;
        TextView user_name, messages_count;
        CardView projectCard;

        public ViewHolder(View v) {
            super(v);

            projectCard = (CardView) v.findViewById(R.id.card_view);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MessageActivity.chatId = chats.get(getPosition()).id;
                    MessageActivity.from_id = chats.get(getPosition()).from_id;
                    MessageActivity.to_id = chats.get(getPosition()).to_id;

                    Intent chat = new Intent(projectCard.getContext(), MessageActivity.class);
                    chat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    projectCard.getContext().startActivity(chat);
                }
            });

            avatar = (ImageView) projectCard.findViewById(R.id.avatarImage);
            user_name = (TextView) projectCard.findViewById(R.id.userName);
            messages_count  = (TextView) projectCard.findViewById(R.id.online_status);
            verImage = (ImageView) projectCard.findViewById(R.id.verImage);
            proImage = (ImageView) projectCard.findViewById(R.id.proImage);

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

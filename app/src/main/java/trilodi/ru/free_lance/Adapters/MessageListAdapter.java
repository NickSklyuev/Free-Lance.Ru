package trilodi.ru.free_lance.Adapters;

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
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lance.Models.User;
import trilodi.ru.free_lance.UI.ProfileActivity;
import trilodi.ru.free_lance.Components.AvatarDrawable;
import trilodi.ru.free_lance.Models.Messages;
import trilodi.ru.free_lanceru.R;
import trilodi.ru.free_lance.UI.ProfileFragment;

/**
 * Created by REstoreService on 24.05.15.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    ArrayList<Messages> messages = new ArrayList<Messages>();
    Map<String, User> users = new HashMap<String, User>();

    Picasso mPicasso;
    private com.squareup.picasso.Target loadtarget;
    MessageListAdapter.ViewHolder h;

    public MessageListAdapter(ArrayList<Messages> messages, Map<String, User> users){
        this.messages = messages;
        this.users = users;
    }

    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_ellement, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MessageListAdapter.ViewHolder holder, int i) {
        Messages chat = this.messages.get(i);

        h = holder;

        User user = users.get(chat.from_id);

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
                        h.avatar.requestLayout();
                    }catch(Exception e){
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
        try {
            if(!user.avatar_url.equals("")){
                mPicasso.load(user.avatar_url).into(loadtarget);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        String username = "";

        try {
            username = user.firstname;
            if(!user.lastname.equals("")){
                //userName.setText(project.user.firstname+" "+project.user.lastname);
                username = user.firstname+" "+user.lastname;
            }
            if(!user.username.equals("")){
                //userName.setText(project.user.firstname+" "+project.user.lastname+" ("+project.user.username+")");
                username = user.firstname+" "+user.lastname+" ("+user.username+")";
            }
        }catch (Exception e){
            e.printStackTrace();
        }




        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        long timestamp = Long.parseLong(chat.create_time) * 1000;
        java.util.Date netDate = (new java.util.Date(timestamp));

        String uName = username.trim() +" ";
        String cDate = sdf.format(netDate);
        Spannable wordtoSpan = new SpannableString(uName+cDate);
        wordtoSpan.setSpan(new ForegroundColorSpan(FreeLanceApplication.getContext().getResources().getColor(R.color.red_color)), uName.length(), (uName.length())+cDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new StyleSpan(Typeface.NORMAL), uName.length(), (uName.length())+cDate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        DisplayMetrics displayMetrics = holder.user_name.getContext().getResources().getDisplayMetrics();
        int px = Math.round(11 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        wordtoSpan.setSpan(new AbsoluteSizeSpan(px), uName.length(), (uName.length())+cDate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //holder.messages_count.setText(wordtoSpan);
        holder.user_name.setText(wordtoSpan);

        holder.messages_count.setText(Html.fromHtml(chat.text).toString());
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case

        ImageView avatar;
        TextView user_name, messages_count;
        CardView projectCard;

        public ViewHolder(View v) {
            super(v);

            projectCard = (CardView) v.findViewById(R.id.card_view);

            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MessageActivity.chatId = chats.get(getPosition()).id;
                    MessageActivity.from_id = chats.get(getPosition()).from_id;
                    MessageActivity.to_id = chats.get(getPosition()).to_id;

                    Intent chat = new Intent(projectCard.getContext(), MessageActivity.class);
                    chat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    projectCard.getContext().startActivity(chat);
                }
            });*/

            avatar = (ImageView) projectCard.findViewById(R.id.avatarImage);
            user_name = (TextView) projectCard.findViewById(R.id.userName);
            messages_count  = (TextView) projectCard.findViewById(R.id.online_status);

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment.userId = messages.get(getPosition()).from_id;

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

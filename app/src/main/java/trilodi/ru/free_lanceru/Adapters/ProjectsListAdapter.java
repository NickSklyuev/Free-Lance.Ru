package trilodi.ru.free_lanceru.Adapters;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.wapstart.plus1.sdk.Plus1BannerAsker;
import ru.wapstart.plus1.sdk.Plus1BannerView;
import ru.wapstart.plus1.sdk.Plus1Request;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.Project;
import trilodi.ru.free_lanceru.R;
import trilodi.ru.free_lanceru.UI.ProjectActivity;

/**
 * Created by REstoreService on 24.05.15.
 */
public class ProjectsListAdapter extends RecyclerView.Adapter<ProjectsListAdapter.ViewHolder> {
    ArrayList<Project> projects;
    String[] currency={"USD","EURO","р."};
    String[] dimension={"","/Час","/День","/Месяц","/Проект"};
    OnItemClickListener mItemClickListener;

    Plus1BannerAsker mAsker;

    public ProjectsListAdapter(ArrayList<Project> projects){
        this.projects = projects;
    }

    @Override
    public ProjectsListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.projects_list_ellement, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ProjectsListAdapter.ViewHolder holder, int i) {
        Project project = this.projects.get(i);


        String description = project.descr;
        description = Html.fromHtml(description).toString();
        if (description.length() > 150) {
            description = description.substring(0, 150) + "...";
        }
        holder.title.setText(Html.fromHtml(project.title).toString());
        holder.descr.setText(description);

        //holder.title.setText(project.title);
        //holder.descr.setText(project.descr);


        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        long timestamp = (long)project.create_time * 1000;
        java.util.Date netDate = (new java.util.Date(timestamp));


        String price="По договоренности";

        if(!project.currency.equals("0")){
            price=project.budget+" "+currency[Integer.parseInt(project.currency)]+dimension[Integer.parseInt(project.dimension)];
        }

        holder.price.setText(price);
        holder.date.setText(sdf.format(netDate));

        if(project.only_pro==0){
            holder.only_pro.setVisibility(View.GONE);
        }else{
            holder.only_pro.setVisibility(View.VISIBLE);
        }

        if(project.only_verified==0){
            holder.only_verified.setVisibility(View.GONE);
        }else{
            holder.only_verified.setVisibility(View.VISIBLE);
        }

        holder.mBannerView.setVisibility(View.GONE);

        if(i%4==0&&i>0){
            holder.mBannerView.setVisibility(View.VISIBLE);
            mAsker =
                    new Plus1BannerAsker(
                            new Plus1Request()
                                    .setApplicationId(12777),
                            holder.mBannerView
                                    .enableAnimationFromTop()
                                    .enableCloseButton()
                    )
                            .setCallbackUrl("wsp1bart://ru.wapstart.plus1.bart")
                            .setRefreshDelay(10); // default value

            holder.mBannerView
                    .addListener(new Plus1BannerView.OnShowListener() {
                        public void onShow(Plus1BannerView pbv) {
                            Log.d("BartActivity", "OnShowListener was touched");
                        }
                    })
                    .addListener(new Plus1BannerView.OnHideListener() {
                        public void onHide(Plus1BannerView pbv) {
                            Log.d("BartActivity", "OnHideListener was touched");
                        }
                    })
                    .addListener(new Plus1BannerView.OnCloseButtonListener() {
                        public void onCloseButton(Plus1BannerView pbv) {
                            Log.d("BartActivity", "OnCloseButtonListener was touched");
                        }
                    })
                    .addListener(new Plus1BannerView.OnImpressionListener() {
                        public void onImpression(Plus1BannerView pbv) {
                            Log.d("BartActivity", "OnImpressionListener was touched");
                        }
                    })
                    .addListener(new Plus1BannerView.OnTrackClickListener() {
                        public void onTrackClick(Plus1BannerView pbv) {
                            Log.d("BartActivity", "OnTrackClickListener was touched");
                        }
                    });

            mAsker.refreshBanner();
        }else{
            holder.mBannerView.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return this.projects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        CardView projectCard;
        TextView title,price,descr, date;
        RelativeLayout only_pro, only_verified;

        Plus1BannerView mBannerView;
        public ViewHolder(View v) {
            super(v);

            projectCard = (CardView) v.findViewById(R.id.card_view);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(projects.get(getPosition()).title);
                    Config.project_id = projects.get(getPosition()).id;
                    Intent intt = new Intent(projectCard.getContext(), ProjectActivity.class);
                    intt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    projectCard.getContext().startActivity(intt);
                }
            });

            title = (TextView) projectCard.findViewById(R.id.titleText);
            descr = (TextView) projectCard.findViewById(R.id.descrText);
            price = (TextView) projectCard.findViewById(R.id.priceText);
            date = (TextView) projectCard.findViewById(R.id.dateText);

            only_pro = (RelativeLayout) projectCard.findViewById(R.id.onlypro);
            only_verified = (RelativeLayout) projectCard.findViewById(R.id.onlyverified);

            mBannerView = (Plus1BannerView) v.findViewById(R.id.plus1BannerView);



        }

        @Override
        public void onClick(View v) {
            System.out.println("Taped");
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }
}

package trilodi.ru.free_lanceru.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Models.Portfolio;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PortfolioActivityFragment extends Fragment {

    ArrayList<String> categories = new ArrayList<String>();
    public static ArrayList<Portfolio> portolios = new ArrayList<Portfolio>();

    ViewPager viewPager;
    SmartTabLayout viewPagerTab;

    public static String user_id="0";

    ImageView backButton;

    ProgressBarCircularIndeterminate progressIndicator;

    AlertDialog messageDialog;
    View loadingView, messageView;
    TextView messageTitle, messageText;

    public PortfolioActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_portfolio, container, false);

        messageView = getActivity().getLayoutInflater().inflate(R.layout.message_dialog_layout, null);

        messageTitle = (TextView) messageView.findViewById(R.id.dialogtitle);
        messageText = (TextView) messageView.findViewById(R.id.dialogDescription);

        backButton = (ImageView) v.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        progressIndicator = (ProgressBarCircularIndeterminate) v.findViewById(R.id.dialogProgress);
        progressIndicator.setVisibility(View.GONE);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) v.findViewById(R.id.viewpagertab);

        messageTitle.setText("ОШИБКА!");
        messageText.setText("Извините, но у пользователя не обнаружено портфолио!");
        messageDialog = new AlertDialog.Builder(getActivity()).setView(messageView).setCancelable(true).create();

        messageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getActivity().finish();
            }
        });


        loadPortfolio();


        return v;
    }


    public void loadPortfolio(){
        portolios = new ArrayList<Portfolio>();
        categories = new ArrayList<String>();


        progressIndicator.setVisibility(View.VISIBLE);

        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("user_id", user_id);
        localRequestParams.put("method", "users_portfolio");
        NetManager.getInstance(getActivity()).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);
                    JSONObject obj = new JSONObject(str);

                    if (obj.get("error").toString().equals("0")) {
                        JSONArray objJson = obj.getJSONObject("data").getJSONArray("porfolio");
                        for (int i = 0; i < objJson.length(); i++) {
                            Portfolio portfolio = new Portfolio(objJson.getJSONObject(i));

                            if (!categories.contains(portfolio.category_id)) {
                                categories.add(portfolio.category_id);
                            }

                            portolios.add(portfolio);

                            renderPortfolio();

                        }

                    }else{
                        Intent splash = new Intent(getActivity(), SplashScreenActivity.class);
                        splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(splash);
                        getActivity().finish();
                    }

                    //renderingView(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                //progDailog.dismiss();
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //new MessagesDialog(PortfolioActivity.this,"Портфолио", "Во время загрузки портфолио произошла ошибка соединения.\nПроверьте соединение и повторите попытку.");
            }
        });
    }

    public void renderPortfolio(){

        FragmentPagerItems.Creator fpic = FragmentPagerItems.with(getActivity());

        for(int i=0;i<categories.size();i++){
            Cursor c = Config.db.query("category", null, "id="+categories.get(i).toString(), null, null, null, null);

            if (c.moveToFirst()) {
                int ut = c.getColumnIndex("title");

                CharSequence cs = c.getString(ut);

                fpic.add(c.getString(ut),PortfolioCatFragment.newInstance(categories.get(i)).getClass(),new Bundler().putString("catId", categories.get(i).toString()).get());


            }
            c.close();
        }


        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getActivity().getSupportFragmentManager(), fpic.create());

        viewPager.setAdapter(adapter);

        viewPagerTab.setViewPager(viewPager);
        progressIndicator.setVisibility(View.GONE);

        if(categories.size()==0){
            messageDialog.show();
        }
    }
}

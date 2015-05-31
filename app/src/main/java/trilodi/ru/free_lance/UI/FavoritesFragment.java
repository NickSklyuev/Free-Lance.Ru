package trilodi.ru.free_lance.UI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appodeal.ads.Appodeal;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.analytics.HitBuilders;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lance.Adapters.FavoritesListAdapter;
import trilodi.ru.free_lance.Components.DBOpenHelper;
import trilodi.ru.free_lance.Config;
import trilodi.ru.free_lance.Models.FavoriteUser;
import trilodi.ru.free_lance.Network.NetManager;
import trilodi.ru.free_lanceru.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    private ImageView MenuButton;


    private OnFragmentInteractionListener mListener;

    private RecyclerView favoritesRecyclerView;
    private FavoritesListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SwipeRefreshLayout refreshLayout;

    ProgressBarCircularIndeterminate loadProgressing;

    ArrayList<FavoriteUser> favorites = new ArrayList<FavoriteUser>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.dbHelper=new DBOpenHelper(getActivity());
        Config.db = Config.dbHelper.getWritableDatabase();
        FreeLanceApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open").setLabel("Список избранного").build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);
        MenuButton = (ImageView) v.findViewById(R.id.MenuButton);
        MenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        loadProgressing  =(ProgressBarCircularIndeterminate) v.findViewById(R.id.dialogProgress);
        loadProgressing.setVisibility(View.GONE);

        favoritesRecyclerView = (RecyclerView) v.findViewById(R.id.projectList);
        favoritesRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        favoritesRecyclerView.setLayoutManager(mLayoutManager);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);

        loadFavorites();

        return v;
    }


    public void loadFavorites(){

        loadProgressing.setVisibility(View.VISIBLE);

        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("method", "users_favorites_list");
        NetManager.getInstance(getActivity()).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, "UTF-8");
                    System.out.println(str);


                    JSONObject r = new JSONObject(str);

                    if(!r.get("error").toString().equals("0")){
                        Intent splash = new Intent(getActivity(),SplashScreenActivity.class);
                        splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(splash);
                        getActivity().finish();
                    }

                    JSONObject localJSONObject = new JSONObject(str);
                    JSONArray localJSONArray = localJSONObject.getJSONObject("data").getJSONArray("favorites_list");

                    for(int i=0; i<localJSONArray.length(); i++){
                        favorites.add(new FavoriteUser(localJSONArray.getJSONObject(i)));
                    }

                    mAdapter = new FavoritesListAdapter(favorites);
                    favoritesRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    favoritesRecyclerView.setAdapter(mAdapter);
                    refreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                loadProgressing.setVisibility(View.GONE);
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                loadProgressing.setVisibility(View.GONE);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume(){
        super.onResume();
        Appodeal.hide(getActivity(), Appodeal.BANNER_VIEW);
        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(getActivity(), Appodeal.BANNER_VIEW);
    }

}

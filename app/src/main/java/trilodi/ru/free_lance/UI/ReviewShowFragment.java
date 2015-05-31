package trilodi.ru.free_lance.UI;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appodeal.ads.Appodeal;

import java.util.ArrayList;

import trilodi.ru.free_lance.Adapters.ReviewListAdapter;
import trilodi.ru.free_lance.Models.UserReview;
import trilodi.ru.free_lanceru.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReviewShowFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReviewShowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewShowFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    String rate = "0";

    private RecyclerView reviewRecyclerView;
    private ReviewListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ArrayList<UserReview> allReviews = new ArrayList<UserReview>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReviewShowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewShowFragment newInstance(String r) {
        ReviewShowFragment fragment = new ReviewShowFragment();
        Bundle args = new Bundle();
        args.putString("rate", r);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewShowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FreeLanceApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open").setLabel("Отзывы о пользователе").build());

        if (getArguments() != null) {
            rate = getArguments().getString("rate");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_review_show, container, false);

        reviewRecyclerView = (RecyclerView) v.findViewById(R.id.responsesList);
        reviewRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        reviewRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<UserReview> reviews = new ArrayList<UserReview>();

        for(int i=0; i<allReviews.size(); i++){
            UserReview r = allReviews.get(i);

            if(r.rate.equals(rate)){
                reviews.add(r);
            }

        }


        mAdapter = new ReviewListAdapter(reviews);
        reviewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        reviewRecyclerView.setAdapter(mAdapter);

        // Inflate the layout for this fragment
        return v;
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

    @Override
    public void onResume(){
        super.onResume();
        Appodeal.hide(getActivity(), Appodeal.BANNER_VIEW);
        //Appodeal.setBannerViewId(R.id.appodealBannerView);
        //Appodeal.show(this, Appodeal.BANNER_VIEW);
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

}

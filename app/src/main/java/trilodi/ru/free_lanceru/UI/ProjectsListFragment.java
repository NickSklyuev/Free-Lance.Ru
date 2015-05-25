package trilodi.ru.free_lanceru.UI;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import trilodi.ru.free_lanceru.Adapters.ProjectsListAdapter;
import trilodi.ru.free_lanceru.Models.Project;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectsListFragment extends Fragment {

    public static ArrayList<Project> projects = new ArrayList<Project>();

    private ImageView MenuButton;

    private RecyclerView projectsRecyclerView;
    private ProjectsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SwipeRefreshLayout refreshLayout;

    private OnFragmentInteractionListener mListener;
    android.support.v7.widget.Toolbar tb;

    ProgressBarCircularIndeterminate loadProgressing;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProjectsListFragment newInstance() {
        ProjectsListFragment fragment = new ProjectsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void hideViews() {
        tb.animate().translationY(-tb.getHeight()).setInterpolator(new AccelerateInterpolator(2));

        FrameLayout.LayoutParams ps = (FrameLayout.LayoutParams) refreshLayout.getLayoutParams();
        ps.topMargin = 0;
        refreshLayout.setLayoutParams(ps);
        refreshLayout.requestLayout();
    }

    private void showViews() {
        tb.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(57 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        FrameLayout.LayoutParams ps = (FrameLayout.LayoutParams) refreshLayout.getLayoutParams();
        ps.topMargin = px;
        refreshLayout.setLayoutParams(ps);
        refreshLayout.requestLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_projects_list, container, false);

        loadProgressing  =(ProgressBarCircularIndeterminate) v.findViewById(R.id.dialogProgress);
        loadProgressing.setVisibility(View.GONE);

        MenuButton = (ImageView) v.findViewById(R.id.MenuButton);
        MenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        tb = (android.support.v7.widget.Toolbar) v.findViewById(R.id.login_toolbar);

        projectsRecyclerView = (RecyclerView) v.findViewById(R.id.projectList);
        projectsRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        projectsRecyclerView.setLayoutManager(mLayoutManager);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
        /*
        projectsRecyclerView.setOnScrollListener(new HidingScrollListener(getActivity()){
            @Override
            public void onHide() {
                hideViews();
            }
            @Override
            public void onShow() {
                showViews();
            }
        });
        */


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        refreshLayout.showContextMenu();

        if(projects.size()<=0){
            loadData();
        }else{
            mAdapter = new ProjectsListAdapter(projects);
            projectsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            projectsRecyclerView.setAdapter(mAdapter);
            refreshLayout.setRefreshing(false);
        }


        return v;
    }


    private void loadData()
    {
        projects.clear();
        refreshLayout.setRefreshing(true);
        loadProgressing.refreshDrawableState();
        loadProgressing.requestLayout();
        loadProgressing.setVisibility(View.VISIBLE);

        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("method", "projects_list");
        localRequestParams.put("page", 1);
        NetManager.getInstance(getActivity()).post(localRequestParams, new AsyncHttpResponseHandler()
        {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String str = new String(responseBody, "UTF-8");

                    JSONObject localJSONObject = new JSONObject(str);
                    JSONArray ProjectsList = localJSONObject.getJSONObject("data").getJSONArray("projects_list");



                    try{
                        for (int i=0; i<ProjectsList.length(); i++){
                            //this.Project(projectsArray.getJSONObject(i));
                            projects.add(new Project(ProjectsList.getJSONObject(i)));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    mAdapter = new ProjectsListAdapter(projects);
                    projectsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    projectsRecyclerView.setAdapter(mAdapter);
                    refreshLayout.setRefreshing(false);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(){
                try {
                    //progDailog.dismiss();
                    loadProgressing.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                loadProgressing.setVisibility(View.GONE);
                //new MessagesDialog(getActivity(),"Проекты", "Во время загрузки списка проектов произошла ошибка соединения.\nПроверьте соединение и повторите попытку.").show();
            }
        });
        //setRefreshActionButtonState(true);
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

}

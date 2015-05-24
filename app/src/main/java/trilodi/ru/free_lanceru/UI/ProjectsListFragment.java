package trilodi.ru.free_lanceru.UI;

import android.app.Activity;
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

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;

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

    ArrayList<Project> projects = new ArrayList<Project>();

    private ImageView MenuButton;

    private RecyclerView projectsRecyclerView;
    private ProjectsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SwipeRefreshLayout refreshLayout;

    private OnFragmentInteractionListener mListener;

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

    @Subscribe
    public void onSetProjects(ArrayList<Project> projects){
        try {
            this.projects = projects;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_projects_list, container, false);
        MenuButton = (ImageView) v.findViewById(R.id.MenuButton);
        MenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        projectsRecyclerView = (RecyclerView) v.findViewById(R.id.projectList);
        projectsRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(projectsRecyclerView.getContext());
        projectsRecyclerView.setLayoutManager(mLayoutManager);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);

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

            mAdapter.SetOnItemClickListener(new ProjectsListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                            /*Intent intt = new Intent(getActivity(), ChatActivity.class);
                            intt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intt);*/
                }
            });
        }


        return v;
    }


    private void loadData()
    {
        projects.clear();
        refreshLayout.setRefreshing(true);

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

                    mAdapter.SetOnItemClickListener(new ProjectsListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            /*Intent intt = new Intent(getActivity(), ChatActivity.class);
                            intt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intt);*/
                        }
                    });

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(){
                try {
                    //progDailog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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

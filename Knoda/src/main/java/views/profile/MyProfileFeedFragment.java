package views.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;

import java.util.ArrayList;

import adapters.PagingAdapter;
import adapters.PredictionAdapter;
import butterknife.InjectView;
import models.Prediction;
import models.ServerError;
import networking.NetworkListCallback;
import pubsub.ActivitiesViewedEvent;
import views.core.BaseListFragment;
import views.details.DetailsFragment;

public class MyProfileFeedFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<Prediction> {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    View topview;
    int screenNumber;
    boolean pageLoaded = false;
    PredictionAdapter predictionAdapter;

    public MyProfileFeedFragment() {
    }

    public static MyProfileFeedFragment newInstance(int id) {
        MyProfileFeedFragment fragment = new MyProfileFeedFragment();
        Bundle b = new Bundle();
        b.putInt("pageNumber", id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        Bundle b = getArguments();
        this.screenNumber = b.getInt("pageNumber", R.id.activity_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activitytype, container, false);
        topview = view;
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("ProfileFeed");
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new ActivitiesViewedEvent());
        pListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadPage(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        pListView.setShowViewWhileRefreshing(false);
        loadPage(0);

    }

    public void loadPage(final int page) {
        if (pageLoaded) {
            return;
        }
        pageLoaded = true;
        boolean challenged = (screenNumber == 1) ? true : false;

        networkingManager.getPredictions(challenged, new NetworkListCallback<Prediction>() {
            @Override
            public void completionHandler(ArrayList<Prediction> object, ServerError error) {
                pListView.setShowIndicator(false);
                pListView.onRefreshComplete();
                if (error != null) {
                    Toast.makeText(getActivity(), "Error getting predictions", Toast.LENGTH_SHORT).show();
                } else {
                    adapter = getAdapter();
                    pListView.setAdapter(adapter);
                    adapter.loadPage(page);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
        pageLoaded = false;
    }

    @Override
    public PagingAdapter getAdapter() {
        predictionAdapter = new PredictionAdapter(getActivity(), this, networkingManager.getImageLoader(), bus, true);
        return predictionAdapter;
    }


    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        boolean challenged = (screenNumber == 1) ? true : false;
        pageLoaded = true;
        networkingManager.getPredictions(challenged, callback);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DetailsFragment fragment = DetailsFragment.newInstance(predictionAdapter.getItem(i));
                pushFragment(fragment);
            }
        });
    }

    @Override
    public String noContentString() {
        pListView.setBackgroundColor(Color.WHITE);
        return "No Predictions";
    }

}
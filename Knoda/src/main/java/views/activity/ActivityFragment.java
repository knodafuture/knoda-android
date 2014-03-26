package views.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;

import adapters.ActivityAdapter;
import adapters.PagingAdapter;
import models.ActivityItem;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.ActivitiesViewedEvent;
import views.core.BaseListFragment;
import views.details.DetailsFragment;

public class ActivityFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<ActivityItem> {

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }
    public ActivityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("ActivityFeed");
        setTitle("ACTIVITY");
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new ActivitiesViewedEvent());
        adapter.loadPage(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
    }

    @Override
    public PagingAdapter getAdapter() {
        return new ActivityAdapter(getActivity(), this, networkingManager.getImageLoader());
    }


    @Override
    public void getObjectsAfterObject(ActivityItem object, NetworkListCallback<ActivityItem> callback) {
        int lastId = object == null ? 0 : object.id;
        networkingManager.getActivityItemsAfter(lastId, callback);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ActivityItem activityItem = (ActivityItem)adapter.getItem(i-1);
                if (activityItem != null) {
                    networkingManager.getPrediction(activityItem.predictionId, new NetworkCallback<Prediction>() {
                        @Override
                        public void completionHandler(Prediction prediction, ServerError error) {
                            DetailsFragment fragment = DetailsFragment.newInstance(prediction);
                            pushFragment(fragment);
                        }
                    });
                }
            }
        });
    }

    @Override
    public String noContentString() {
        return "No Activity";
    }

}

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
import unsorted.Logger;
import views.core.BaseListFragment;
import views.details.DetailsFragment;

public class ActivityFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<ActivityItem> {

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }
    public ActivityFragment() {}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("ActivityFeed");
        getActivity().getActionBar().setTitle("Activity");

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
                Logger.log("Clicked at : " + i);
                ActivityItem activityItem = (ActivityItem)adapter.getItem(i-1);
                networkingManager.getPrediction(activityItem.predictionId, new NetworkCallback<Prediction>() {
                    @Override
                    public void completionHandler(Prediction prediction, ServerError error) {
                        DetailsFragment fragment = new DetailsFragment(prediction);
                        pushFragment(fragment);
                    }
                });
            }
        });
    }

}

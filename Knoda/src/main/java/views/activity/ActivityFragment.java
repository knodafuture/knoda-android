package views.activity;

import android.os.Bundle;
import android.view.View;

import adapters.ActivityAdapter;
import adapters.PagingAdapter;
import networking.NetworkListCallback;
import models.ActivityItem;
import views.core.BaseListFragment;

public class ActivityFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<ActivityItem> {

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }
    public ActivityFragment() {}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

}

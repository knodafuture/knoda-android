package views.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.knoda.knoda.R;

import adapters.ActivityAdapter;
import adapters.PagingAdapter;
import butterknife.InjectView;
import core.networking.NetworkListCallback;
import models.ActivityItem;
import views.core.BaseFragment;

public class ActivityFragment extends BaseFragment implements PagingAdapter.PagingAdapterDatasource<ActivityItem> {

    ActivityAdapter adapter;

    @InjectView(R.id.activity_listview) ListView listView;

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }
    public ActivityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ActivityAdapter(getActivity().getLayoutInflater(), this, networkingManager.getImageLoader());
        listView.setAdapter(adapter);

        adapter.loadPage(0);


    }

    @Override
    public void getObjectsAfterObject(ActivityItem object, NetworkListCallback<ActivityItem> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getActivityItemsAfter(lastId, callback);

    }

}

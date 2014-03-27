package views.group;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;

import adapters.GroupAdapter;
import adapters.PagingAdapter;
import models.Group;
import networking.NetworkListCallback;
import views.core.BaseFragment;
import views.core.BaseListFragment;
import views.predictionlists.GroupPredictionListFragment;

public class GroupFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<Group> {
    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("Groups_Screen");
        setTitle("GROUPS");
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.loadPage(0);
    }

    @Override
    public PagingAdapter getAdapter() {
        return new GroupAdapter(getActivity(), this, networkingManager.getImageLoader());
    }


    @Override
    public void getObjectsAfterObject(Group object, NetworkListCallback<Group> callback) {
        //networkingManager.getGroups(callback);
        callback.completionHandler(userManager.groups, null);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BaseFragment fragment;
                if (view instanceof CreateGroupHeaderView) {
                    fragment = AddGroupFragment.newInstance();
                } else {
                    GroupListCell v = ((GroupListCell)view);
                    fragment = GroupPredictionListFragment.newInstance(v.group);
                }
                pushFragment(fragment);
            }
        });
    }



    @Override
    public String noContentString() {
        return "No Groups";
    }
}



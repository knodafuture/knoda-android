package views.group;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import adapters.GroupAdapter;
import adapters.PagingAdapter;
import butterknife.InjectView;
import models.Group;
import networking.NetworkListCallback;
import views.core.BaseFragment;
import views.core.MainActivity;
import views.predictionlists.GroupPredictionListFragment;

public class GroupFragment extends BaseFragment implements PagingAdapter.PagingAdapterDatasource<Group> {

    @InjectView(R.id.group_list_view)
    ListView listView;

    PagingAdapter adapter;

    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle b) {
        setHasOptionsMenu(true);
        super.onCreate(b);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("Groups_Screen");
        setTitle("GROUPS");

        adapter = new GroupAdapter(getActivity(), this, networkingManager.getImageLoader());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BaseFragment fragment;
                if (view instanceof CreateGroupHeaderView) {
                    fragment = AddGroupFragment.newInstance();
                } else {
                    if (view instanceof GroupListCell) {
                        GroupListCell v = ((GroupListCell) view);
                        fragment = GroupPredictionListFragment.newInstance(v.group);

                    } else {
                        fragment = AddGroupFragment.newInstance();
                    }
                    pushFragment(fragment);

                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        getActivity().invalidateOptionsMenu();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).resetNavIcons();
        getActivity().findViewById(R.id.nav_groups).setBackgroundResource(R.drawable.nav_groups_active);
        ((TextView) getActivity().findViewById(R.id.nav_groups_text)).setTextColor(Color.parseColor("#EFEFEF"));
        adapter.loadPage(0);
    }

    @Override
    public void getObjectsAfterObject(Group object, NetworkListCallback<Group> callback) {
        //networkingManager.getGroups(callback);
        callback.completionHandler(userManager.groups, null);
    }


    @Override
    public String noContentString() {
        return "You are not in any groups. Create a group with your friends to start making private predictions.";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (((MainActivity) getActivity()).userManager.getUser().guestMode == false)
            inflater.inflate(R.menu.groups, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        if (((MainActivity) getActivity()).userManager.getUser().guestMode == false) {
//            menu.removeItem(R.id.action_search);
//            if (((MainActivity) getActivity()).currentFragment.equals(this.getClass().getSimpleName()) && menu.findItem(R.id.action_create_group) != null)
//                menu.findItem(R.id.action_create_group).setVisible(true);
//        } else {
//            if (((MainActivity) getActivity()).currentFragment.equals(this.getClass().getSimpleName()) && menu.findItem(R.id.action_create_group) != null)
//                menu.findItem(R.id.action_create_group).setVisible(false);
//        }
        super.onPrepareOptionsMenu(menu);
    }
}



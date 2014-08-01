package views.contests;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;

import adapters.ContestAdapter;
import adapters.PagingAdapter;
import butterknife.InjectView;
import models.Contest;
import networking.NetworkListCallback;
import pubsub.GroupNavEvent;
import views.core.BaseListFragment;
import views.core.MainActivity;

public class ContestFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<Contest> {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    View topview;
    String filter = "";
    boolean pageLoaded = false;

    public ContestFragment() {
    }

    public static ContestFragment newInstance(String filter) {
        ContestFragment fragment = new ContestFragment();
        Bundle b = new Bundle();
        b.putString("filter", filter);
        fragment.setArguments(b);
        return fragment;
    }

    @Subscribe
    public void groupNav(final GroupNavEvent event) {
        if (listView != null)
            listView.smoothScrollToPosition(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle b = getArguments();
        this.filter = b.getString("filter", "entered");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        topview = getView();
        getActivity().invalidateOptionsMenu();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("ContestFragment");
        if (filter.equals("explore")) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("EXPLORE");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (filter.equals("explore"))
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
        pageLoaded = false;
    }

    @Override
    public PagingAdapter getAdapter() {
        ContestAdapter adapter1 = new ContestAdapter(getActivity(), this, networkingManager.getImageLoader());
        adapter1.mainActivity = (MainActivity) getActivity();
        return adapter1;
    }

    @Override
    public void getObjectsAfterObject(Contest contest, NetworkListCallback<Contest> callback) {
        pageLoaded = true;
        networkingManager.getContests(filter, callback);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Contest contest = (Contest) adapter.getItem(i - 1);
                if (contest != null) {
                    if (view.getId() == R.id.contest_standings_container) {
                        ContestLeaderboardFragment fragment = ContestLeaderboardFragment.newInstance(contest);
                        ((MainActivity) getActivity()).pushFragment(fragment);
                    } else {
                        ContestDetailFragment fragment = ContestDetailFragment.newInstance(contest);
                        ((MainActivity) getActivity()).pushFragment(fragment);
                    }


                }
//                if (activityItem != null) {
//                    if (activityItem.type == ActivityItemType.INVITATION) {
//
//                    } else {
////                        if (activityItem.type == ActivityItemType.WON && view.getId() == R.id.winloss_button) {
////
////                        }
//                    }
//                }
            }
        });
    }

    @Override
    public String noContentString() {
        pListView.setBackgroundColor(Color.WHITE);
        if (filter.equals("entered"))
            return "You have not entered in any contests. Explore new contests and make predictions";
        else if (filter.equals("explore"))
            return "There are no contests running right now.";
        return "No contests";
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (filter.equals("entered")) {
            inflater.inflate(R.menu.social, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        menu.clear();
    }

}
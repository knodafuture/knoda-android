package views.contests;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;

import adapters.ContestLeaderboardUserAdapter;
import adapters.PagingAdapter;
import butterknife.InjectView;
import models.ContestStage;
import models.ContestUser;
import networking.NetworkListCallback;
import views.core.BaseListFragment;

public class ContestLeaderboardFeedFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<ContestUser> {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    View topview;
    boolean pageLoaded = false;
    ContestStage contestStage;

    public ContestLeaderboardFeedFragment() {
    }

    public static ContestLeaderboardFeedFragment newInstance(ContestStage contestStage) {
        ContestLeaderboardFeedFragment fragment = new ContestLeaderboardFeedFragment();
        fragment.contestStage = contestStage;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        topview = getView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("ContestLeaderboardFeedFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
        pageLoaded = false;
    }

    @Override
    public PagingAdapter getAdapter() {
        return new ContestLeaderboardUserAdapter(getActivity(), this, networkingManager.getImageLoader());
    }

    @Override
    public void getObjectsAfterObject(ContestUser contestuser, NetworkListCallback<ContestUser> callback) {
        pageLoaded = true;
        networkingManager.getContestLeaderboard(contestStage.contest_id, (contestStage.id == -1) ? null : contestStage.id, callback);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    public String noContentString() {
        pListView.setBackgroundColor(Color.WHITE);
        return "There are no users ranked in this contest.";
    }

}
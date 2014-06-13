package views.activity;
import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;

import adapters.LeaderboardAdapter;
import adapters.PagingAdapter;
import models.Leader;
import networking.NetworkListCallback;
import views.core.BaseListFragment;

public class ActivityBoardFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<Leader> {
    public String board;

    public static ActivityBoardFragment newInstance( String board) {
        ActivityBoardFragment fragment = new ActivityBoardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("BOARD", board.toUpperCase());
        fragment.setArguments(bundle);
        return fragment;
    }

    public ActivityBoardFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        board = getArguments().getString("BOARD");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("Activity board " + board);
    }

    @Override
    public PagingAdapter getAdapter() {
        return new LeaderboardAdapter(getActivity(), this, networkingManager.getImageLoader());
    }

    @Override
    public void getObjectsAfterObject(Leader object, NetworkListCallback<Leader> callback) {
        //networkingManager.getGroupLeaderboard(group.id, board, callback);
    }

    @Override
    public String noContentString() {
        return "No activity here";
    }
}
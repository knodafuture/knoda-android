package views.group;

import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;

import adapters.LeaderboardAdapter;
import adapters.PagingAdapter;
import factories.GsonF;
import models.Group;
import models.Leader;
import networking.NetworkListCallback;
import views.core.BaseListFragment;

public class GroupLeaderboardFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<Leader> {
    public Group group;
    public String board;

    public GroupLeaderboardFragment() {
    }

    public static GroupLeaderboardFragment newInstance(Group group, String board) {
        GroupLeaderboardFragment fragment = new GroupLeaderboardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP", GsonF.actory().toJson(group));
        bundle.putString("BOARD", board.toUpperCase());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
        board = getArguments().getString("BOARD");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(group.name.toUpperCase());
        FlurryAgent.logEvent("GROUP_LEADERBOARD_" + board);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public PagingAdapter getAdapter() {
        return new LeaderboardAdapter(getActivity(), this, networkingManager.getImageLoader());
    }

    @Override
    public void getObjectsAfterObject(Leader object, NetworkListCallback<Leader> callback) {
        networkingManager.getGroupLeaderboard(group.id, board, callback);
    }

    @Override
    public String noContentString() {
        return "No Members";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }
}

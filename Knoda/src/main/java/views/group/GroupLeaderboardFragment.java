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

    public static GroupLeaderboardFragment newInstance(Group group) {
        GroupLeaderboardFragment fragment = new GroupLeaderboardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP", GsonF.actory().toJson(group));
        fragment.setArguments(bundle);
        return fragment;
    }

    public GroupLeaderboardFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(group.name.toUpperCase());
        FlurryAgent.logEvent("Group_Leaderboard");
    }

    @Override
    public PagingAdapter getAdapter() {
        return new LeaderboardAdapter(getActivity(), this, networkingManager.getImageLoader());
    }

    @Override
    public void getObjectsAfterObject(Leader object, NetworkListCallback<Leader> callback) {
        networkingManager.getGroupLeaderboard(group.id, callback);
    }

    @Override
    public String noContentString() {
        return "No Members";
    }
}

package views.predictionlists;

import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import adapters.GroupPredictionAdapter;
import adapters.PagingAdapter;
import factories.GsonF;
import models.Group;
import models.Prediction;
import networking.NetworkListCallback;
import views.group.GroupLeaderboardFragment;

public class GroupPredictionListFragment extends BasePredictionListFragment {
    public Group group;

    public static GroupPredictionListFragment newInstance(Group group) {
        GroupPredictionListFragment fragment = new GroupPredictionListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP", GsonF.actory().toJson(group));
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(group.name.toUpperCase());
        final GroupPredictionAdapter a = ((GroupPredictionAdapter)adapter);
        a.setGroup(group);
        a.setLoadFinishedListener(new PagingAdapter.PagingAdapaterPageLoadFinishListener<Prediction>() {
            @Override
            public void adapterFinishedLoadingPage(int page) {
                a.header.findViewById(R.id.group_rankings_container).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GroupLeaderboardFragment fragment = GroupLeaderboardFragment.newInstance(group);
                        pushFragment(fragment);
                    }
                });
                a.header.findViewById(R.id.group_settings_container).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorReporter.showError("Show Settings");
                    }
                });
            }
        });
        FlurryAgent.logEvent("Group_Prediction_List");
    }

    @Override
    public PagingAdapter getAdapter() {
        return new GroupPredictionAdapter(getActivity(), this, networkingManager.getImageLoader());
    }

    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getPredictionsForGroupAfter(group.id, lastId, callback);
    }

    @Override
    public void onItemClicked(int position) {
        position = position - 1;
        if (position <= 0) {
            return;
        }
        super.onItemClicked(position);
    }
}

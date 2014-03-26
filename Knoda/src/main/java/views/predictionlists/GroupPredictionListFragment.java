package views.predictionlists;

import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;

import adapters.GroupPredictionAdapter;
import adapters.PagingAdapter;
import factories.GsonF;
import models.Group;
import models.Prediction;
import networking.NetworkListCallback;

/**
 * Created by adamengland on 3/26/14.
 */
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
        ((GroupPredictionAdapter)adapter).setGroup(group);
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
}

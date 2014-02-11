package views.predictionlists;

import android.os.Bundle;
import android.view.View;

import models.Prediction;
import models.Tag;
import networking.NetworkListCallback;

/**
 * Created by nick on 2/11/14.
 */
public class CategoryFragment extends BasePredictionListFragment {

    Tag tag;

    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
        return fragment;
    }
    public CategoryFragment() {}

    public CategoryFragment(Tag tag) {
        super();
        this.tag = tag;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getActionBar().setTitle(tag.name.toUpperCase());
    }

    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getPredictionsWithTagAfter(tag, lastId, callback);
    }
}

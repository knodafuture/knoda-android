package views.predictionlists;

import android.os.Bundle;
import android.view.View;

import models.Prediction;
import networking.NetworkListCallback;

/**
 * Created by nick on 2/3/14.
 */
public class HistoryFragment extends BasePredictionListFragment {

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }
    public HistoryFragment() {}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getActionBar().setTitle("History");
    }

    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getHistoryAfter(lastId, callback);
    }



}

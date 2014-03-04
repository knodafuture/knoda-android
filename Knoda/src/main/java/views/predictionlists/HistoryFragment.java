package views.predictionlists;

import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import factories.GsonF;
import models.Prediction;
import models.ServerError;
import networking.NetworkListCallback;
import unsorted.Logger;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle("HISTORY");
        FlurryAgent.logEvent("History");
    }

    @Override
    public void getObjectsAfterObject(Prediction object, final NetworkListCallback<Prediction> callback) {
        int lastId = (object == null || object.challenge == null) ? 0 : object.challenge.id;

        networkingManager.getHistoryAfter(lastId, new NetworkListCallback<Prediction>() {
            @Override
            public void completionHandler(ArrayList<Prediction> object, ServerError error) {
                Logger.log(GsonF.actory().toJson(object));
                callback.completionHandler(object, error);
            }
        });
    }

    @Override
    public String noContentString() {
        return "Make your first prediction or vote.";
    }
}

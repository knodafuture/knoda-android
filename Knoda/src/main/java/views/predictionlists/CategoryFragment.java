package views.predictionlists;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

import models.Prediction;
import networking.NetworkListCallback;

/**
 * Created by nick on 2/11/14.
 */
public class CategoryFragment extends BasePredictionListFragment {

    String tag;

    public CategoryFragment() {
    }

    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
        return fragment;
    }

    public static CategoryFragment newInstance(String tag) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TAG", tag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("TAG")) {
            tag = getArguments().getString("TAG");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(tag.toUpperCase());
        Map<String, String> flurryParams = new HashMap<String, String>();
        flurryParams.put("Category", tag.toUpperCase());
        FlurryAgent.logEvent("Predictions_By_Category_Screen", flurryParams);
    }

    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getPredictionsWithTagAfter(tag, lastId, callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }
}

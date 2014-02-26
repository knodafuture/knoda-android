package views.predictionlists;

import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import pubsub.NewPredictionEvent;

public class HomeFragment extends BasePredictionListFragment {

    @Subscribe public void newPrediction(NewPredictionEvent event) {
        getAdapter().insertAt(event.prediction, 0);
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
    public HomeFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getActionBar().setTitle("Home");
        FlurryAgent.logEvent("Home_Screen");
    }
}

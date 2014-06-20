package views.predictionlists;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;

import pubsub.UserChangedEvent;

public class HomeFragment extends BasePredictionListFragment {

    @Subscribe
    public void userChanged(final UserChangedEvent event) {
        adapter.loadPage(0);
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bus.register(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPrefManager.setFirstLaunch(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle("HOME");
        FlurryAgent.logEvent("Home_Screen");
    }

    @Override
    protected void onLoadFinished(){
        if (adapter.currentPage == 0 && !sharedPrefManager.getFirstLaunch() && !sharedPrefManager.haveShownPredictionWalkthrough())
            showPredictionWalkthrough();
    }

    @Override
    public void onPredictionDisagreed(final PredictionListCell cell) {
        hideTour();
        super.onPredictionDisagreed(cell);
    }
    @Override
    public void onPredictionAgreed(final PredictionListCell cell) {
        hideTour();
        super.onPredictionAgreed(cell);
    }


    private void showPredictionWalkthrough() {
        final Handler animHandler = new Handler();
        animHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.view_predict_walkthrough, null);
                Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeingrow);
                ((ViewGroup) getView()).addView(v);
                v.startAnimation(fadeInAnimation);
                listView.setTag(v);
                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeoutshrink);
                        v.startAnimation(fadeOutAnimation);
                        v.setVisibility(View.INVISIBLE);
                        listView.setTag(null);
                        return true;
                    }
                });
            }
        }, 750);

    }


    private void hideTour() {
        if (listView.getTag() != null) {
            final RelativeLayout walkthrough = ((RelativeLayout) listView.getTag());
            listView.setTag(null);
            walkthrough.setVisibility(View.INVISIBLE);
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeoutshrink);
            walkthrough.startAnimation(fadeOutAnimation);

            final Handler animHandler = new Handler();
            animHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams lp = walkthrough.getLayoutParams();
                    lp.height = 0;
                    walkthrough.setLayoutParams(lp);

                    if (!userManager.getUser().guestMode)
                        showPredictionWalkthrough();

                }
            }, 500);
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        if(listView.getTag()!=null){
            View walkthrough = ((View) listView.getTag());
            ViewGroup.LayoutParams lp = walkthrough.getLayoutParams();
            lp.height = 0;
            walkthrough.setLayoutParams(lp);
            ((View)listView.getTag()).setVisibility(View.INVISIBLE);
            listView.setTag(null);
            sharedPrefManager.setHaveShownPredictionWalkthrough(true);
        }
    }


}

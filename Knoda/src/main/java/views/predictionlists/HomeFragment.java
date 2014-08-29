package views.predictionlists;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;

import adapters.PagingAdapter;
import adapters.PredictionAdapter;
import managers.SharedPrefManager;
import models.Prediction;
import networking.NetworkListCallback;
import pubsub.HomeNavEvent;
import pubsub.LoginFlowDoneEvent;
import pubsub.ReloadListsEvent;
import views.contacts.FindFriendsActivity;
import views.core.MainActivity;
import views.search.SearchFragment;

public class HomeFragment extends BasePredictionListFragment implements HomeActionBar.HomeActionBarCallbacks {
    HomeActionBar homeActionBar;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Subscribe
    public void homeNav(final HomeNavEvent event) {
        if (listView != null)
            listView.smoothScrollToPosition(0);
    }

    @Override
    public PagingAdapter getAdapter() {
        return new PredictionAdapter(getActivity(), this, networkingManager.getImageLoader(), bus, this);
    }

    @Subscribe
    public void refreshList(final ReloadListsEvent event) {
        adapter.loadPage(0);
    }

    @Subscribe
    public void loginDone(final LoginFlowDoneEvent event) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bus.register(this);
        sharedPrefManager.saveObjectString(0, SharedPrefManager.SAVED_HOMESCREEN_SELECTED);
    }

    @Override
    public void getObjectsAfterObject(Prediction object, final NetworkListCallback<Prediction> callback) {
        if (homeActionBar == null || homeActionBar.selected == 0) {
            int lastId = object == null ? 0 : object.id;
            networkingManager.getPredictionsAfter(lastId, callback);
        } else
            networkingManager.getFollowFeed(callback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.home_actionbar);
        homeActionBar = (HomeActionBar) menuItem.getActionView();
        getActivity().getActionBar().setDisplayShowHomeEnabled(false);
        getActivity().getActionBar().setDisplayUseLogoEnabled(false);
        getActivity().getActionBar().setDisplayShowTitleEnabled(false);
        homeActionBar.setCallbacks(this);
        homeActionBar.selected = Integer.parseInt(sharedPrefManager.getObjectString(SharedPrefManager.SAVED_HOMESCREEN_SELECTED));
        homeActionBar.setFilter(homeActionBar.selected);
        adapter.reset();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle("HOME");
        FlurryAgent.logEvent("Home_Screen");
    }

    @Override
    protected void onLoadFinished() {
        if (adapter.currentPage == 0 && !sharedPrefManager.getFirstLaunch() && !sharedPrefManager.haveShownPredictionWalkthrough() && !userManager.getUser().guestMode && userManager.getUser().points > 0)
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
                if (getActivity() == null || getView() == null)
                    return;
                sharedPrefManager.setHaveShownPredictionWalkthrough(true);
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.view_predict_walkthrough, null);
                Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeingrow);
                ((ViewGroup) (getView()).findViewById(R.id.home_overlay)).addView(v);
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
            sharedPrefManager.setFirstLaunch(false);
            sharedPrefManager.setShouldShowVotingWalkthrough(false);
            final RelativeLayout walkthrough = ((RelativeLayout) listView.getTag());
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
                    listView.setTag(null);
                    if (!userManager.getUser().guestMode)
                        showPredictionWalkthrough();

                }
            }, 500);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listView.getTag() != null) {
            View walkthrough = ((View) listView.getTag());
            ViewGroup.LayoutParams lp = walkthrough.getLayoutParams();
            lp.height = 0;
            walkthrough.setLayoutParams(lp);
            ((View) listView.getTag()).setVisibility(View.INVISIBLE);
            listView.setTag(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).resetNavIcons();
        getActivity().findViewById(R.id.nav_home).setBackgroundResource(R.drawable.nav_home_active);
        ((TextView) getActivity().findViewById(R.id.nav_home_text)).setTextColor(Color.parseColor("#EFEFEF"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayShowHomeEnabled(true);
        getActivity().getActionBar().setDisplayUseLogoEnabled(true);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public String noContentString() {
        return "No Predictions.";
    }


    @Override
    public void onSearchClick() {
        pushFragment(new SearchFragment());
    }

    @Override
    public void onAddFriendsClick() {
        Intent intent = new Intent(getActivity(), FindFriendsActivity.class);
        intent.putExtra("cancelable", true);
        startActivity(intent);
    }

    @Override
    public void onSwitchFeed(int number) {
        sharedPrefManager.saveObjectString(number, SharedPrefManager.SAVED_HOMESCREEN_SELECTED);
        pListView.setRefreshing(true);
        adapter.loadPage(0);
    }
}

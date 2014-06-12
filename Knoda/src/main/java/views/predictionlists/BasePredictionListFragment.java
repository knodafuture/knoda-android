package views.predictionlists;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.ArrayList;

import adapters.PagingAdapter;
import adapters.PredictionAdapter;
import butterknife.InjectView;
import listeners.PredictionSwipeListener;
import models.KnodaScreen;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import views.core.BaseListFragment;
import views.core.MainActivity;
import views.details.DetailsFragment;
import views.login.WelcomeFragment;

/**
 * Created by nick on 2/3/14.
 */
public class BasePredictionListFragment extends BaseListFragment implements PredictionSwipeListener.PredictionCellCallbacks, PagingAdapter.PagingAdapterDatasource<Prediction> {

    PredictionSwipeListener swipeListener;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public PagingAdapter getAdapter() {
        return new PredictionAdapter(getActivity(), this, networkingManager.getImageLoader(), bus);
    }

    @Override
    public AbsListView.OnScrollListener getOnScrollListener() {
        return swipeListener.makeScrollListener();
    }

    @Override
    public void onListViewCreated(ListView listView) {
        swipeListener = new PredictionSwipeListener(listView, this);
        listView.setOnTouchListener(swipeListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked(i);
            }
        });
    }

    public void onItemClicked(int position) {
        Prediction prediction = (Prediction) adapter.getItem(position - 1);
        if (prediction != null) {
            DetailsFragment fragment = DetailsFragment.newInstance(prediction);
            pushFragment(fragment);
        }
    }

    @Override
    public void getObjectsAfterObject(Prediction object, final NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getPredictionsAfter(lastId, callback);

    }

    private void hideTour() {
        if (listView.getTag() != null) {
            final RelativeLayout walkthrough=((RelativeLayout) listView.getTag());
            listView.setTag(null);
            walkthrough.setVisibility(View.INVISIBLE);
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
            walkthrough.startAnimation(fadeOutAnimation);

            final Handler animHandler = new Handler();
            animHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams lp =walkthrough.getLayoutParams();
                    lp.height=0;
                    walkthrough.setLayoutParams(lp);
                    animHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService
                                    (Context.LAYOUT_INFLATER_SERVICE);
                            View v = inflater.inflate(R.layout.view_predict_walkthrough,null);
                            Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                            listView.addHeaderView(v);
                            v.startAnimation(fadeInAnimation);
                        }
                    },750);
                }
            },250);


        }
    }

    @Override
    public void onPredictionAgreed(final PredictionListCell cell) {
        cell.setAgree(true);
        hideTour();
        if (userManager.getUser() == null) {
            WelcomeFragment f = WelcomeFragment.newInstance();
            f.show(getActivity().getFragmentManager(), "welcome");
            return;
        }
        

        networkingManager.agreeWithPrediction(cell.prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                if (error != null)
                    errorReporter.showError(error);
                else {
                    cell.prediction = object;
                    cell.update();
                    ((MainActivity) getActivity()).checkBadges();
                }
            }
        });
        FlurryAgent.logEvent("Swiped_Agree");
    }

    @Override
    public void onPredictionDisagreed(final PredictionListCell cell) {
        cell.setAgree(false);
        hideTour();
        if (userManager.getUser() == null) {
            WelcomeFragment f = WelcomeFragment.newInstance();
            f.show(getActivity().getFragmentManager(), "welcome");
            return;
        }

        networkingManager.disagreeWithPrediction(cell.prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                if (error != null) {
                    errorReporter.showError(error);
                } else {
                    cell.prediction = object;
                    cell.update();
                    ((MainActivity) getActivity()).checkBadges();
                }
            }
        });
        FlurryAgent.logEvent("Swiped_Disagree");
    }

    @Override
    public void onProfileTapped(final PredictionListCell cell) {
        if (cell.prediction.userId.equals(userManager.getUser().id)) {
            ((MainActivity) getActivity()).showFrament(KnodaScreen.KnodaScreenOrder.PROFILE);
        } else {
            AnotherUsersProfileFragment fragment = AnotherUsersProfileFragment.newInstance(cell.prediction.userId);
            pushFragment(fragment);
        }
    }

    @Override
    public String noContentString() {
        return "";
    }
}

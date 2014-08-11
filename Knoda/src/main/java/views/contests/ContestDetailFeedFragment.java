package views.contests;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;

import adapters.PagingAdapter;
import adapters.PredictionAdapter;
import butterknife.InjectView;
import models.Prediction;
import networking.NetworkListCallback;
import views.predictionlists.BasePredictionListFragment;
import views.predictionlists.PredictionListCell;

public class ContestDetailFeedFragment extends BasePredictionListFragment implements PagingAdapter.PagingAdapterDatasource<Prediction> {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    View topview;
    boolean pageLoaded = false;
    PredictionAdapter predictionAdapter;
    ContestDetailFragment parentFragment;
    String filter;
    int contestId;

    boolean resizing = false;
    Handler h;
    int visible, scroll_state, headerSize = 0;

    public ContestDetailFeedFragment() {
    }

    public static ContestDetailFeedFragment newInstance(String filter, int contestId, ContestDetailFragment parentFragment) {
        ContestDetailFeedFragment fragment = new ContestDetailFeedFragment();
        fragment.parentFragment = parentFragment;
        fragment.filter = filter;
        fragment.contestId = contestId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        h = new Handler();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topview = getView();
        FlurryAgent.logEvent("ContestDetailFeed");
    }

    @Override
    public void onResume() {
        super.onResume();
        //resizeHeader(0);
    }

    @Override
    public AbsListView.OnScrollListener getOnScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                scroll_state = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!parentFragment.loaded || resizing == true || scroll_state == SCROLL_STATE_IDLE)
                    return;
                resizing = true;
                if (visible == 0)
                    visible = visibleItemCount;
                if (firstVisibleItem > 1 && headerSize != 1) {
                    resizeHeader(1);
                } else if (firstVisibleItem == 0) {
                    resizeHeader(0);
                } else {
                    resizing = false;
                    return;
                }
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resizing = false;
                    }
                }, 800);

            }
        };
    }

    public void resizeHeader(int state) {
        if (headerSize == state) {
            return;
        }
        headerSize = state;
        if (state == 0) {
            final ExpandAnimation expandAnimation = new ExpandAnimation(parentFragment.topContainerHeight);
            parentFragment.header.startAnimation(expandAnimation);
        } else if (state == 1) {
            final ExpandAnimation expandAnimation = new ExpandAnimation(0);
            parentFragment.header.startAnimation(expandAnimation);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        pageLoaded = false;
    }

    @Override
    public void onDestroy() {
        parentFragment = null;
        super.onDestroy();
    }


    @Override
    public PagingAdapter getAdapter() {
        predictionAdapter = new PredictionAdapter(getActivity(), this, networkingManager.getImageLoader(), bus, true);
        predictionAdapter.showContestTour = true;
        return predictionAdapter;
    }


    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        if (filter != null && filter.equals("expired"))
            networkingManager.getContestsPredictions(contestId, true, callback);
        else
            networkingManager.getContestsPredictions(contestId, false, callback);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        super.onListViewCreated(listView);
    }

    @Override
    public String noContentString() {
        pListView.setBackgroundColor(Color.WHITE);
        return "No Predictions for this contest.";
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

    private void hideTour() {
        parentFragment.hidePredictWalkthrough();
    }


    private class ExpandAnimation extends Animation {
        private final int mStartHeight;
        private final int mDeltaHeight;
        LinearLayout.LayoutParams lp;

        public ExpandAnimation(int endHeight) {
            mStartHeight = (endHeight == parentFragment.topContainerHeight) ? 0 : parentFragment.topContainerHeight;
            mDeltaHeight = mStartHeight - endHeight;
            this.setDuration(400);
            lp = parentFragment.params;
        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            lp.height = (int) (mStartHeight - (mDeltaHeight * interpolatedTime));
            parentFragment.header.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

}
package views.contests;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;

import adapters.PagingAdapter;
import adapters.PredictionAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import helpers.ListenerHelper;
import listeners.PredictionSwipeListener;
import models.Contest;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.PredictionChangeEvent;
import views.core.BaseFragment;
import views.core.BaseWebFragment;
import views.core.MainActivity;
import views.details.DetailsFragment;
import views.predictionlists.PredictionListCell;

public class ContestDetailFragment extends BaseFragment implements PredictionSwipeListener.PredictionCellCallbacks, PagingAdapter.PagingAdapterDatasource<Prediction> {

    public boolean loaded = false;
    TextView selectedFilter;
    View selectedUnderline;
    @InjectView(R.id.topview)
    LinearLayout topview;
    @InjectView(R.id.contest_detail_header)
    RelativeLayout header;
    Contest contest;
    LinearLayout.LayoutParams params;
    int topContainerHeight;
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    View walkthrough1 = null;

    int visible, scroll_state, headerSize = 0;
    boolean resizing = false;
    Handler h;

    @InjectView(R.id.pListview)
    PullToRefreshListView pListview;

    PredictionSwipeListener swipeListener;
    PredictionAdapter predictionAdapter;
    String filter = "";

    public static ContestDetailFragment newInstance(Contest contest) {
        ContestDetailFragment fragment = new ContestDetailFragment();
        fragment.contest = contest;
        return fragment;
    }

    @OnClick(R.id.activity_1)
    void onClick1() {
        if (!filter.equals("")) {
            resizeHeader(0);
            filter = "";
            pListview.setRefreshing();
            changeFilter(R.id.activity_1);
        }
    }

    @OnClick(R.id.activity_2)
    void onClick2() {
        if (!filter.equals("expired")) {
            resizeHeader(0);
            filter = "expired";
            predictionAdapter.reset();
            pListview.setRefreshing();
            changeFilter(R.id.activity_2);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        sharedPrefManager.setShouldShowContestVotingWalkthrough(true);
        h = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_detail, container, false);
        ButterKnife.inject(this, view);
        getActivity().invalidateOptionsMenu();
        swipeListener = new PredictionSwipeListener(pListview.getRefreshableView(), this);

        selectedFilter = (TextView) view.findViewById(R.id.activity_1);
        selectedUnderline = view.findViewById(R.id.underline_1);
        setTitle("DETAILS");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ContestListCell listItem = (ContestListCell) LayoutInflater.from(getActivity()).inflate(R.layout.list_cell_contest, null);
        listItem.setContest(contest, (MainActivity) getActivity());
        listItem.setHeaderMode();

        LinearLayout.LayoutParams title_normal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams title_no_image = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getActivity().getResources().getDisplayMetrics());
        title_no_image.setMargins(onedp * 15, onedp * 15, onedp * 15, 0);
        title_normal.setMargins(onedp * 15, onedp * 5, onedp * 15, 0);

        if (contest.avatar != null) {
            listItem.avatarImageView.setImageUrl(contest.avatar.big, networkingManager.getImageLoader());
            listItem.titleTV.setLayoutParams(title_normal);
        } else {
            listItem.findViewById(R.id.contest_avatar_container).setVisibility(View.GONE);
            listItem.titleTV.setLayoutParams(title_no_image);
        }

        header.addView(listItem);

        if (sharedPrefManager.shouldShowContestVotingWalkthrough() && contest.contestMyInfo == null) {
            final android.os.Handler h = new android.os.Handler();
            final View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_contest_predict_walkthrough, null);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slidein);
            v.startAnimation(fadeInAnimation);
            header.addView(v);
            header.setLayoutParams(lp);
            walkthrough1 = v;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    hidePredictWalkthrough();
                }
            });
        }
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        FlurryAgent.logEvent("ContestDetail_Screen");

        pListview.setShowIndicator(false);
        pListview.getRefreshableView().setDivider(null);
        pListview.setRefreshing(true);
        predictionAdapter = (PredictionAdapter) getAdapter();

        pListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                predictionAdapter.loadPage(0);
                resizeHeader(0);
            }
        });

        pListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prediction prediction = predictionAdapter.getItem(position - 1);
                if (prediction != null) {
                    DetailsFragment fragment = DetailsFragment.newInstance(prediction);
                    pushFragment(fragment);
                }
            }
        });

        pListview.getRefreshableView().setOnTouchListener(swipeListener);
        pListview.setRefreshing();
        addScrollListener();

    }

    public PagingAdapter getAdapter() {
        predictionAdapter = new PredictionAdapter(getActivity(), this, networkingManager.getImageLoader(), bus, true);
        predictionAdapter.showContestTour = true;

        predictionAdapter.setLoadFinishedListener(new PagingAdapter.PagingAdapaterPageLoadFinishListener() {
            @Override
            public void adapterFinishedLoadingPage(int page) {
                pListview.onRefreshComplete();
                //onLoadFinished();
                if (getActivity() != null)
                    ((MainActivity) getActivity()).invalidateBackgroundImage();
            }
        });


        return predictionAdapter;
    }

    public void hidePredictWalkthrough() {
        if (walkthrough1 != null) {
            sharedPrefManager.setShouldShowContestVotingWalkthrough(false);
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeoutshrink);
            walkthrough1.startAnimation(fadeOutAnimation);
            walkthrough1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    walkthrough1.setVisibility(View.INVISIBLE);
                    walkthrough1 = null;
                    addVotedWalkthrough();
                }
            }, 500);
        }
    }

    private void addVotedWalkthrough() {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_contest_voted_walkthrough, null);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slidein);
        v.startAnimation(fadeInAnimation);
        final Handler h = new Handler();
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeoutshrink);
                v.startAnimation(fadeOutAnimation);
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.GONE);
                    }
                }, 500);
            }
        });
        ((RelativeLayout) getView().findViewById(R.id.contest_walkthrough_container)).addView(v);
    }


    @Override
    public void onResume() {
        super.onResume();
        header.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                topContainerHeight = header.getHeight();
                params = (LinearLayout.LayoutParams) header.getLayoutParams();
                header.removeOnLayoutChangeListener(this);
                loaded = true;
            }
        });
        pListview.setAdapter(predictionAdapter);
        filter = "";
        predictionAdapter.loadPage(0);
    }

    @Override
    public void onPause() {
        loaded = false;
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.contestdetails, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void changeFilter(int id) {
        selectedUnderline.setVisibility(View.INVISIBLE);
        switch (id) {
            case R.id.activity_1:
                selectedUnderline = topview.findViewById(R.id.underline_1);
                break;
            case R.id.activity_2:
                selectedUnderline = topview.findViewById(R.id.underline_2);
                break;
        }
        selectedFilter.setTextColor(getResources().getColor(R.color.knodaLighterGreen));
        selectedFilter = ((TextView) topview.findViewById(id));
        selectedFilter.setTextColor(Color.WHITE);
        selectedUnderline.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contest_info: {
                pushFragment(BaseWebFragment.newInstance(contest.detail_url, "DETAILS", false));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPredictionAgreed(final PredictionListCell cell) {
        hidePredictWalkthrough();
        cell.setAgree(true);
        networkingManager.agreeWithPrediction(cell.prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                if (error != null)
                    errorReporter.showError(error);
                else {
                    cell.prediction = object;
                    cell.update();
                    bus.post(new PredictionChangeEvent(object));
                }
            }
        });
        FlurryAgent.logEvent("Swiped_Agree");
    }

    @Override
    public void onPredictionDisagreed(final PredictionListCell cell) {
        hidePredictWalkthrough();
        cell.setAgree(false);
        networkingManager.disagreeWithPrediction(cell.prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                if (error != null) {
                    errorReporter.showError(error);
                } else {
                    cell.prediction = object;
                    cell.update();
                    bus.post(new PredictionChangeEvent(object));
                }
            }
        });
        FlurryAgent.logEvent("Swiped_Disagree");
    }

    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        if (filter != null && filter.equals("expired"))
            networkingManager.getContestsPredictions(contest.id, true, callback);
        else
            networkingManager.getContestsPredictions(contest.id, false, callback);
    }

    @Override
    public String noContentString() {
        return "No predictions for this contest.";
    }

    public AbsListView.OnScrollListener getOnScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                scroll_state = scrollState;
                boolean enabled = scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
                swipeListener.paused = !enabled;
                if (swipeListener.paused)
                    swipeListener.resetSwipe();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!loaded || resizing == true || scroll_state == SCROLL_STATE_IDLE)
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

    private void addScrollListener() {

        AbsListView.OnScrollListener listener = getOnScrollListener();

        if (listener == null)
            pListview.getRefreshableView().setOnScrollListener(predictionAdapter.makeScrollListener());
        else
            pListview.getRefreshableView().setOnScrollListener(ListenerHelper.concatListeners(listener, predictionAdapter.makeScrollListener()));
    }

    public void resizeHeader(int state) {
        if (headerSize == state) {
            return;
        }
        headerSize = state;
        if (state == 0) {
            final ExpandAnimation expandAnimation = new ExpandAnimation(topContainerHeight);
            header.startAnimation(expandAnimation);
        } else if (state == 1) {
            final ExpandAnimation expandAnimation = new ExpandAnimation(0);
            header.startAnimation(expandAnimation);
        }

    }

    private class ExpandAnimation extends Animation {
        private final int mStartHeight;
        private final int mDeltaHeight;
        LinearLayout.LayoutParams lp;

        public ExpandAnimation(int endHeight) {
            mStartHeight = (endHeight == topContainerHeight) ? 0 : topContainerHeight;
            mDeltaHeight = mStartHeight - endHeight;
            this.setDuration(400);
            lp = params;
        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            lp.height = (int) (mStartHeight - (mDeltaHeight * interpolatedTime));
            header.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }


}

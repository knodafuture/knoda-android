package views.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import adapters.PagingAdapter;
import adapters.PredictionAdapter;
import butterknife.InjectView;
import factories.GsonF;
import factories.TypeTokenFactory;
import models.Prediction;
import models.ServerError;
import networking.NetworkListCallback;
import pubsub.ProfileNavEvent;
import pubsub.ProfilePagerScrollEvent;
import views.core.BaseListFragment;
import views.details.DetailsFragment;
import views.predictionlists.PredictionListCell;

public class MyProfileFeedFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<Prediction> {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    View topview;
    int screenNumber;
    boolean pageLoaded = false;
    PredictionAdapter predictionAdapter;
    MyProfileFragment parentFragment;

    boolean resizing = false;
    android.os.Handler h;
    int visible, scroll_state, headerSize = 0;

    public MyProfileFeedFragment() {
    }

    public static MyProfileFeedFragment newInstance(int id, MyProfileFragment parentfragment) {
        MyProfileFeedFragment fragment = new MyProfileFeedFragment();
        fragment.parentFragment = parentfragment;
        Bundle b = new Bundle();
        b.putInt("pageNumber", id);
        fragment.setArguments(b);
        return fragment;
    }

    @Subscribe
    public void pagerScroll(ProfilePagerScrollEvent event) {
        if (listView != null)
            listView.smoothScrollToPosition(0);
    }

    @Subscribe
    public void profileNav(final ProfileNavEvent event) {
        if (listView != null)
            listView.smoothScrollToPosition(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        Bundle b = getArguments();
        this.screenNumber = b.getInt("pageNumber", R.id.activity_1);
        h = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activitytype, container, false);
        topview = view;
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("ProfileFeed");
    }

    @Override
    public void onResume() {
        super.onResume();
        resizeHeader(0);
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

    public void loadPage(final int page) {
//        if (pageLoaded) {
//            return;
//        }
//        pageLoaded = true;
//        boolean challenged = (screenNumber == 1) ? true : false;
//
//        networkingManager.getPredictions(challenged, new NetworkListCallback<Prediction>() {
//            @Override
//            public void completionHandler(ArrayList<Prediction> object, ServerError error) {
//                pListView.setShowIndicator(false);
//                pListView.onRefreshComplete();
//                if (error != null) {
//                    Toast.makeText(getActivity(), "Error getting predictions", Toast.LENGTH_SHORT).show();
//                } else {
//                    adapter = getAdapter();
//                    pListView.setAdapter(adapter);
//                    adapter.loadPage(page);
//                }
//            }
//        });
    }

    public void resizeHeader(int state) {
        if (headerSize == state) {
            return;
        }
        headerSize = state;
        if (state == 0) {
            final ExpandAnimation expandAnimation = new ExpandAnimation(parentFragment.topContainerHeight);
            parentFragment.topContainer.startAnimation(expandAnimation);
        } else if (state == 1) {
            final ExpandAnimation expandAnimation = new ExpandAnimation(0);
            parentFragment.topContainer.startAnimation(expandAnimation);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
        pageLoaded = false;
    }

    @Override
    public PagingAdapter getAdapter() {
        predictionAdapter = new PredictionAdapter(getActivity(), this, networkingManager.getImageLoader(), bus, true);
        String cachedObject = sharedPrefManager.getObjectString(screenNumber + "profile");
        if (cachedObject != null) {
            Gson gson = GsonF.actory();
            ArrayList<Prediction> cachedContest = gson.fromJson(cachedObject, TypeTokenFactory.getPredictionListTypeToken().getType());
            predictionAdapter.setCachedObjects(cachedContest);
        }

        return predictionAdapter;
    }


    @Override
    public void getObjectsAfterObject(Prediction object, final NetworkListCallback<Prediction> callback) {
        boolean challenged = (screenNumber == 1) ? true : false;
        pageLoaded = true;
        int lastId = object == null ? 0 : object.id;
        networkingManager.getPredictionsAfterId(challenged, lastId, new NetworkListCallback<Prediction>() {
            @Override
            public void completionHandler(ArrayList<Prediction> object, ServerError error) {
                callback.completionHandler(object, error);
                if (error == null) {
                    sharedPrefManager.saveObjectString(object, screenNumber + "profile");
                }
            }
        });
    }

    @Override
    public void onListViewCreated(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view instanceof PredictionListCell && (i - 1) >= 0) {
                    DetailsFragment fragment = DetailsFragment.newInstance(predictionAdapter.getItem(i - 1));
                    pushFragment(fragment);
                }
            }
        });
    }

    @Override
    public String noContentString() {
        pListView.setBackgroundColor(Color.WHITE);
        return "No Predictions";
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
            parentFragment.topContainer.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

}
package views.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;

import java.util.ArrayList;

import adapters.ActivityAdapter;
import adapters.PagingAdapter;
import butterknife.InjectView;
import models.ActivityItem;
import models.ActivityItemType;
import models.Invitation;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.ActivitiesViewedEvent;
import views.core.BaseListFragment;
import views.details.DetailsFragment;
import views.group.GroupSettingsFragment;

public class ActivityTypeFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<ActivityItem> {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    View topview;
    int pageNumber;
    boolean pageLoaded = false;

    public ActivityTypeFragment() {
    }

    public static ActivityTypeFragment newInstance(int id) {
        ActivityTypeFragment fragment = new ActivityTypeFragment();
        Bundle b = new Bundle();
        b.putInt("pageNumber", id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        Bundle b = getArguments();
        this.pageNumber = b.getInt("pageNumber", R.id.activity_1);
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
        FlurryAgent.logEvent("ActivityFeed");
        setTitle("ACTIVITY");
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new ActivitiesViewedEvent());
        pListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pListView.setShowIndicator(false);
                loadPage();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        loadPage();
    }

    public void loadPage() {
        if (pageLoaded)
            return;
        pageLoaded = true;
        String filter = null;
        if (pageNumber == 1)
            filter = "expired";
        else if (pageNumber == 2)
            filter = "comments";
        else if (pageNumber == 3)
            filter = "invites";
        networkingManager.getActivityItemsAfter(0, filter, new NetworkListCallback<ActivityItem>() {
            @Override
            public void completionHandler(ArrayList<ActivityItem> object, ServerError error) {
                pListView.onRefreshComplete();
                if (error != null) {
                    Toast.makeText(getActivity(), "Error getting new activities", Toast.LENGTH_SHORT).show();
                } else {
                    adapter = getAdapter();
                    pListView.setAdapter(adapter);
                    adapter.loadPage(0);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
        pageLoaded = false;
    }

    @Override
    public PagingAdapter getAdapter() {
        return new ActivityAdapter(getActivity(), this, networkingManager.getImageLoader(), getActivity());
    }


    @Override
    public void getObjectsAfterObject(ActivityItem object, NetworkListCallback<ActivityItem> callback) {
        int lastId = object == null ? 0 : object.id;
        String filter = null;
        if (pageNumber == 1)
            filter = "expired";
        else if (pageNumber == 2)
            filter = "comments";
        else if (pageNumber == 3)
            filter = "invites";
        pageLoaded = true;
        networkingManager.getActivityItemsAfter(lastId, filter, callback);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ActivityItem activityItem = (ActivityItem) view.getTag();
                if (activityItem != null) {
                    if (activityItem.type == ActivityItemType.INVITATION) {
                        spinner.show();
                        networkingManager.getInvitationByCode(activityItem.target, new NetworkCallback<Invitation>() {
                            @Override
                            public void completionHandler(Invitation invitation, ServerError error) {
                                spinner.hide();
                                GroupSettingsFragment fragment = GroupSettingsFragment.newInstance(invitation.group, activityItem.target);
                                pushFragment(fragment);
                            }
                        });
                    } else {
                        if (activityItem.type == ActivityItemType.WON && view.getId() == R.id.winloss_button) {

                        } else {
                            spinner.show();
                            networkingManager.getPrediction(Integer.parseInt(activityItem.target), new NetworkCallback<Prediction>() {
                                @Override
                                public void completionHandler(final Prediction prediction, ServerError error) {
                                    spinner.hide();
                                    final Handler h = new Handler();
                                    h.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (prediction == null) {
                                                Toast.makeText(getActivity(), "Error loading prediction", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            DetailsFragment fragment = DetailsFragment.newInstance(prediction);
                                            pushFragment(fragment);
                                        }
                                    }, 50);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    public String noContentString() {
        return "No Activity";
    }

}
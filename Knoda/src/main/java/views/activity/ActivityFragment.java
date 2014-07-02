package views.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;

import java.util.ArrayList;

import adapters.ActivityAdapter;
import adapters.PagingAdapter;
import butterknife.InjectView;
import butterknife.OnClick;
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

public class ActivityFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<ActivityItem> {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    String filter = "all";
    TextView selectedFilter;
    View selectedUnderline;
    View topview;

    public ActivityFragment() {
    }

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    @OnClick(R.id.activity_1)
    void onClickAll() {
        changeFilter(R.id.activity_1);
    }

    @OnClick(R.id.activity_2)
    void onClickExpired() {
        changeFilter(R.id.activity_2);
    }

    @OnClick(R.id.activity_3)
    void onClickComments() {
        changeFilter(R.id.activity_3);
    }

    @OnClick(R.id.activity_4)
    void onClickInvite() {
        changeFilter(R.id.activity_4);
    }

    private void changeFilter(int id) {
        selectedUnderline.setVisibility(View.INVISIBLE);
        switch (id) {
            case R.id.activity_1:
                filter = "all";
                selectedUnderline = topview.findViewById(R.id.underline_1);
                break;
            case R.id.activity_2:
                filter = "expired";
                selectedUnderline = topview.findViewById(R.id.underline_2);
                break;
            case R.id.activity_3:
                filter = "comments";
                selectedUnderline = topview.findViewById(R.id.underline_3);
                break;
            case R.id.activity_4:
                filter = "invites";
                selectedUnderline = topview.findViewById(R.id.underline_4);
                break;
        }
        sharedPrefManager.setSavedActivityFilter(id);
        selectedFilter.setTextColor(getResources().getColor(R.color.knodaLighterGreen));
        selectedFilter = ((TextView) topview.findViewById(id));
        selectedFilter.setTextColor(Color.WHITE);
        selectedUnderline.setVisibility(View.VISIBLE);
        adapter = getAdapter();
        pListView.setAdapter(adapter);
        adapter.loadPage(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        topview = view;
        selectedFilter = (TextView) view.findViewById(R.id.activity_1);
        selectedUnderline = view.findViewById(R.id.underline_1);
        sharedPrefManager.setSavedActivityFilter(R.id.activity_1);
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
        changeFilter(sharedPrefManager.getSavedActivityFilter());
        pListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pListView.setShowIndicator(false);
                networkingManager.getActivityItemsAfter(0, new NetworkListCallback<ActivityItem>() {
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
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
    }

    @Override
    public PagingAdapter getAdapter() {
        return new ActivityAdapter(getActivity(), this, networkingManager.getImageLoader(), getActivity(), filter);
    }


    @Override
    public void getObjectsAfterObject(ActivityItem object, NetworkListCallback<ActivityItem> callback) {
        int lastId = object == null ? 0 : object.id;
        networkingManager.getActivityItemsAfter(lastId, callback);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //final ActivityItem activityItem = (ActivityItem) adapter.getItem(i - 1);
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
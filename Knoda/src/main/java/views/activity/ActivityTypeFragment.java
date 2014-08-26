package views.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import adapters.ActivityAdapter;
import adapters.PagingAdapter;
import butterknife.InjectView;
import factories.GsonF;
import factories.TypeTokenFactory;
import models.ActivityItem;
import models.ActivityItemType;
import models.Invitation;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.ActivityNavEvent;
import views.core.BaseListFragment;
import views.core.MainActivity;
import views.details.DetailsFragment;
import views.group.GroupSettingsFragment;

public class ActivityTypeFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<ActivityItem> {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    View topview;
    int screenNumber;
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

    @Subscribe
    public void activityNav(final ActivityNavEvent event) {
        if (listView != null)
            listView.smoothScrollToPosition(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        Bundle b = getArguments();
        this.screenNumber = b.getInt("pageNumber", R.id.activity_1);
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
    }

    public void loadPage(final int page) {
        if (pageLoaded) {
            return;
        }
        pageLoaded = true;
        String filter = null;
        if (screenNumber == 1)
            filter = "expired";
        else if (screenNumber == 2)
            filter = "comments";
        else if (screenNumber == 3)
            filter = "invites";
        networkingManager.getActivityItemsAfter(page, filter, new NetworkListCallback<ActivityItem>() {
            @Override
            public void completionHandler(ArrayList<ActivityItem> object, ServerError error) {
                pListView.setShowIndicator(false);
                pListView.onRefreshComplete();
                if (error != null) {
                    Toast.makeText(getActivity(), "Error getting new activities", Toast.LENGTH_SHORT).show();
                } else {
                    adapter = getAdapter();
                    pListView.setAdapter(adapter);
                    adapter.loadPage(page);
                }
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        pageLoaded = false;
    }

    @Override
    public PagingAdapter getAdapter() {
        ActivityAdapter adapter1 = new ActivityAdapter(getActivity(), this, networkingManager.getImageLoader(), getActivity());
        String cachedObject = sharedPrefManager.getObjectString(screenNumber + "activity");
        if (cachedObject != null) {
            Gson gson = GsonF.actory();
            ArrayList<ActivityItem> cachedContest = gson.fromJson(cachedObject, TypeTokenFactory.getActivityItemTypeToken().getType());
            adapter1.setCachedObjects(cachedContest);
        }
        return adapter1;
    }


    @Override
    public void getObjectsAfterObject(ActivityItem object, final NetworkListCallback<ActivityItem> callback) {
        int lastId = object == null ? 0 : object.id;
        String filter = null;
        if (screenNumber == 1)
            filter = "expired";
        else if (screenNumber == 2)
            filter = "comments";
        else if (screenNumber == 3)
            filter = "invites";
        pageLoaded = true;
        networkingManager.getActivityItemsAfter(lastId, filter, new NetworkListCallback<ActivityItem>() {
            @Override
            public void completionHandler(ArrayList<ActivityItem> object, ServerError error) {
                callback.completionHandler(object, error);
                if (error == null) {
                    sharedPrefManager.saveObjectString(object, screenNumber + "activity");
                }
            }
        });
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
        pListView.setBackgroundColor(Color.WHITE);
        if (screenNumber == 3)
            return "No invitations";
        return "No Activity";
    }

    @Override
    public void onLoadFinished() {
        ((MainActivity) getActivity()).setActivitiesDot(true, true);
    }

}
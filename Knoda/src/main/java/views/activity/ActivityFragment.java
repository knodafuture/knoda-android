package views.activity;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.knoda.knoda.R;

import com.flurry.android.FlurryAgent;

import adapters.ActivityAdapter;
import adapters.PagingAdapter;
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

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    public ActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
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
        adapter.loadPage(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
    }

    @Override
    public PagingAdapter getAdapter() {
        return new ActivityAdapter(getActivity(), this, networkingManager.getImageLoader(), getActivity());
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
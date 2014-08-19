package views.contacts;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.knoda.knoda.R;

import java.util.ArrayList;

import adapters.ActivityAdapter;
import adapters.PagingAdapter;
import adapters.UserContactAdapter;
import models.ActivityItem;
import models.ActivityItemType;
import models.Invitation;
import models.Prediction;
import models.ServerError;
import models.UserContact;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import views.core.BaseListFragment;
import views.core.MainActivity;
import views.details.DetailsFragment;
import views.group.GroupSettingsFragment;

public class FindFriendsFacebookFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<UserContact> {

    FindFriendsActivity parent;
    boolean pageLoaded = false;

    public FindFriendsFacebookFragment() {
    }

    public static FindFriendsFacebookFragment newInstance(FindFriendsActivity parent) {
        FindFriendsFacebookFragment fragment = new FindFriendsFacebookFragment();
        fragment.parent = parent;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent.bus.register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("FindFriendsFacebook");
        pListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
        pageLoaded = false;
    }

    @Override
    public PagingAdapter getAdapter() {
        return new UserContactAdapter(getActivity(), this, parent.networkingManager.getImageLoader());
    }

    @Override
    public void getObjectsAfterObject(UserContact userContact, final NetworkListCallback<UserContact> callback) {
        parent.networkingManager.getFriendsOnKnoda("facebook",callback);
    }

    @Override
    public String noContentString() {
        pListView.setBackgroundColor(Color.WHITE);
        return "No Facebook friends on Knoda";
    }
}
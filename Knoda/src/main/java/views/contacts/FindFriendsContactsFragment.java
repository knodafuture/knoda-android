package views.contacts;

import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import adapters.PagingAdapter;
import adapters.UserContactAdapter;
import models.UserContact;
import networking.NetworkListCallback;
import views.core.BaseListFragment;

public class FindFriendsContactsFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<UserContact> {
    boolean pageLoaded = false;
    FindFriendsActivity parent;

    public FindFriendsContactsFragment() {
    }

    public static FindFriendsContactsFragment newInstance(FindFriendsActivity parent) {
        FindFriendsContactsFragment fragment = new FindFriendsContactsFragment();
        fragment.parent = parent;
        return fragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("FindFriendsContacts");
        pListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.reset();
    }

    @Override
    public PagingAdapter getAdapter() {
        UserContactAdapter adapter1 = new UserContactAdapter(getActivity(), this, parent.networkingManager.getImageLoader());
        return adapter1;

    }

    @Override
    public void getObjectsAfterObject(UserContact object, final NetworkListCallback<UserContact> callback) {
        if (parent.localContacts == null)
            return;
        parent.networkingManager.matchPhoneContacts(parent.localContacts, callback);
    }

    @Override
    public String noContentString() {
        return "There are no contacts";
    }


}
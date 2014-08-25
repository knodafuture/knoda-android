package views.contacts;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import adapters.PagingAdapter;
import adapters.UserContactAdapter;
import models.KnodaInfo;
import models.UserContact;
import networking.NetworkListCallback;
import views.core.BaseListFragment;

public class FindFriendsFacebookTwitterFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<UserContact> {

    FindFriendsActivity parent;
    boolean pageLoaded = false;
    String filter;

    public FindFriendsFacebookTwitterFragment() {
    }

    public static FindFriendsFacebookTwitterFragment newInstance(FindFriendsActivity parent, String filter) {
        FindFriendsFacebookTwitterFragment fragment = new FindFriendsFacebookTwitterFragment();
        fragment.parent = parent;
        fragment.filter = filter;
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
        FlurryAgent.logEvent("FindFriends" + filter);
        pListView.setMode(PullToRefreshBase.Mode.DISABLED);

        if (filter.equals("facebook")) {
            if (((FindFriendsActivity) getActivity()).userManager.getUser().getFacebookAccount() == null) {

            }
        } else if (filter.equals("twitter")) {
            if (((FindFriendsActivity) getActivity()).userManager.getUser().getTwitterAccount() == null) {

            }
        } else {

        }

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
        int type = 0;
        if (filter.equals("facebook"))
            type = FindFriendsListCellHeader.FACEBOOK;
        else if (filter.equals("twitter"))
            type = FindFriendsListCellHeader.TWITTER;
        return new UserContactAdapter(type, getActivity(), this, parent.networkingManager.getImageLoader(), parent);
    }

    @Override
    public void onListViewCreated(ListView listView) {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            }
//        });
    }



    @Override
    public void getObjectsAfterObject(UserContact userContact, final NetworkListCallback<UserContact> callback) {
        parent.networkingManager.getFriendsOnKnoda(filter, callback);
    }

    @Override
    public String noContentString() {
        //pListView.setBackgroundColor(Color.WHITE);
        if (filter.equals("facebook"))
            return "No Facebook friends on Knoda. Get your friends on Knoda!";
        else if (filter.equals("twitter"))
            return "No one you follow on twitter is on Knoda. Get your friends on Knoda!";
        else
            return "No friends on Knoda found. Get your friends on Knoda!";
    }

}
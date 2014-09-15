package views.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;

import adapters.HeadToHeadAdapter;
import adapters.PagingAdapter;
import butterknife.InjectView;
import models.User;
import networking.NetworkListCallback;
import views.core.BaseListFragment;
import views.core.MainActivity;

public class HeadToHeadFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<User> {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    View topview;
    boolean pageLoaded = false;

    public HeadToHeadFragment() {
    }

    public static HeadToHeadFragment newInstance() {
        HeadToHeadFragment fragment = new HeadToHeadFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        topview = getView();
        getActivity().invalidateOptionsMenu();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("HeadToHeadFragment");
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("HEAD-TO-HEAD WINS");

    }

    @Override
    public PagingAdapter getAdapter() {
        return new HeadToHeadAdapter(getActivity(), this, ((MainActivity) getActivity()).networkingManager.getImageLoader(), (MainActivity) getActivity());
    }

    @Override
    public void getObjectsAfterObject(User user, final NetworkListCallback<User> callback) {
        pageLoaded = true;
        networkingManager.getRivals(userManager.getUser().id, callback);
//        networkingManager.getContests(filter, new NetworkListCallback<Contest>() {
//            @Override
//            public void completionHandler(ArrayList<Contest> object, ServerError error) {
//                callback.completionHandler(object, error);
//                if (error == null) {
//                    sharedPrefManager.saveObjectString(object, filter + "contests");
//                }
//            }
//        });
    }

    @Override
    public void onListViewCreated(ListView listView) {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                final Contest contest = (Contest) adapter.getItem(i - 1);
//                if (contest != null) {
//                    if (view.getId() == R.id.contest_standings_container) {
//                        ContestLeaderboardFragment fragment = ContestLeaderboardFragment.newInstance(contest);
//                        ((MainActivity) getActivity()).pushFragment(fragment);
//                    } else {
//                        ContestDetailFragment fragment = ContestDetailFragment.newInstance(contest);
//                        ((MainActivity) getActivity()).pushFragment(fragment);
//                    }
//                }
//            }
//        });
    }

    @Override
    public String noContentString() {
        pListView.setBackgroundColor(Color.WHITE);
        return "No Head to Head matchups";
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}
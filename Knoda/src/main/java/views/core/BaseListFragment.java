package views.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.knoda.knoda.R;

import adapters.PagingAdapter;
import butterknife.InjectView;
import helpers.ListenerHelper;

public class BaseListFragment extends BaseFragment {

    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;

    protected PagingAdapter adapter;
    public ListView listView;

    public static BaseListFragment newInstance() {
        BaseListFragment fragment = new BaseListFragment();
        return fragment;
    }
    public BaseListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = pListView.getRefreshableView();
        onListViewCreated(listView);

        adapter = getAdapter();
        pListView.setAdapter(adapter);

        adapter.loadPage(0);

        addScrollListener();



        adapter.setLoadFinishedListener(new PagingAdapter.PagingAdapaterPageLoadFinishListener() {
            @Override
            public void adapterFinishedLoadingPage(int page) {
                pListView.onRefreshComplete();
            }
        });

        pListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                adapter.loadPage(0);
            }
        });

        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(getActivity());
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        pListView.setOnPullEventListener(soundListener);
    }

    private void addScrollListener() {
        AbsListView.OnScrollListener listener = getOnScrollListener();

        if (listener == null)
            listView.setOnScrollListener(adapter.makeScrollListener());
        else
            listView.setOnScrollListener(ListenerHelper.concatListeners(listener, adapter.makeScrollListener()));
    }

    public PagingAdapter getAdapter() {
        return null;
    }

    public AbsListView.OnScrollListener getOnScrollListener() {
        return null;
    }

    public void onListViewCreated(ListView listView) {

    }
}

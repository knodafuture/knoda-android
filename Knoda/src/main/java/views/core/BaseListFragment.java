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
import com.knoda.knoda.R;

import adapters.PagingAdapter;
import butterknife.InjectView;
import helpers.ListenerHelper;

public class BaseListFragment extends BaseFragment {
    @InjectView(R.id.base_listview)
    public PullToRefreshListView pListView;
    public ListView listView;
    protected PagingAdapter adapter;

    public BaseListFragment() {
    }

    public static BaseListFragment newInstance() {
        BaseListFragment fragment = new BaseListFragment();
        return fragment;
    }

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
        pListView.setShowIndicator(false);
        listView = pListView.getRefreshableView();
        onListViewCreated(listView);

        if (adapter == null) {
            adapter = getAdapter();
            adapter.loadPage(0);
        }
        pListView.setAdapter(adapter);

        addScrollListener();

        adapter.setLoadFinishedListener(new PagingAdapter.PagingAdapaterPageLoadFinishListener() {
            @Override
            public void adapterFinishedLoadingPage(int page) {
                pListView.onRefreshComplete();
                onLoadFinished();
//                if (getActivity() != null && getActivity() instanceof MainActivity)
//                    ((MainActivity) getActivity()).invalidateBackgroundImage();
            }
        });

        pListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                adapter.loadPage(0);
            }
        });
        pListView.setRefreshing(true);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    protected void onLoadFinished() {
    }


}

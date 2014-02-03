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

import com.knoda.knoda.R;

import adapters.PagingAdapter;
import butterknife.InjectView;
import helpers.ListenerHelper;

public class BaseListFragment extends BaseFragment {

    @InjectView(R.id.base_listview)
    public ListView listView;

    protected PagingAdapter adapter;

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

        onListViewCreated(listView);

        adapter = getAdapter();
        listView.setAdapter(adapter);

        adapter.loadPage(0);

        addScrollListener();
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

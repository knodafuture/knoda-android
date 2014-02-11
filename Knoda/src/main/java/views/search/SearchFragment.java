package views.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.knoda.knoda.R;

import adapters.PagingAdapter;
import adapters.TagAdapter;
import butterknife.InjectView;
import models.Tag;
import networking.NetworkListCallback;
import views.core.BaseFragment;
import views.predictionlists.CategoryFragment;

public class SearchFragment extends BaseFragment implements SearchView.SearchViewCallbacks, PagingAdapter.PagingAdapterDatasource<Tag>{

    @InjectView(R.id.search_listview)
    ListView listview;

    TagAdapter adapter;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }
    public SearchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getActionBar().setTitle("");
        menu.removeGroup(R.id.default_menu_group);
        inflater.inflate(R.menu.search, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setCallbacks(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TagAdapter(getActivity().getLayoutInflater(), this, null);

        listview.setAdapter(adapter);

        adapter.loadPage(0);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Tag tag = adapter.getItem(position);
                CategoryFragment fragment = new CategoryFragment(tag);
                pushFragment(fragment);
            }
        });
    }


    @Override
    public void onSearch(String string) {

    }

    @Override
    public void getObjectsAfterObject(Tag object, NetworkListCallback<Tag> callback) {
        networkingManager.getTags(callback);
    }


}

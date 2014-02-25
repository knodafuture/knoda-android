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
import adapters.SearchAdapter;
import adapters.TagAdapter;
import butterknife.InjectView;
import models.Prediction;
import models.Tag;
import models.User;
import networking.NetworkListCallback;
import views.core.BaseFragment;
import views.details.DetailsFragment;
import views.predictionlists.AnotherUsersProfileFragment;
import views.predictionlists.CategoryFragment;

public class SearchFragment extends BaseFragment implements SearchView.SearchViewCallbacks, PagingAdapter.PagingAdapterDatasource<Tag>,
        SearchAdapter.SearchAdapterDatasource, SearchAdapter.SearchAdapterCallbacks {

    @InjectView(R.id.search_listview)
    ListView listview;

    TagAdapter tagAdapter;
    SearchAdapter searchAdapter;
    SearchView searchView;

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
        hideKeyboard();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getActionBar().setTitle("");
        menu.removeGroup(R.id.default_menu_group);
        inflater.inflate(R.menu.search, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (SearchView)menuItem.getActionView();
        searchView.setCallbacks(this);
        showKeyboard(searchView.searchField);

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

        tagAdapter = new TagAdapter(getActivity(), this, null);

        searchAdapter = new SearchAdapter(getActivity(), this, this, networkingManager.getImageLoader());

        onCancel();


    }


    @Override
    public void onSearch(String string) {

        hideKeyboard();

        listview.setAdapter(searchAdapter);

        searchAdapter.loadForSearchTerm(string);

        listview.setOnItemClickListener(searchAdapter.makeOnItemClickListeners());
    }

    @Override
    public  void onCancel() {
        listview.setAdapter(tagAdapter);

        tagAdapter.loadPage(0);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Tag tag = tagAdapter.getItem(position);
                CategoryFragment fragment = new CategoryFragment(tag.name);
                pushFragment(fragment);
            }
        });
    }

    @Override
    public void getObjectsAfterObject(Tag object, NetworkListCallback<Tag> callback) {
        networkingManager.getTags(callback);
    }

    @Override
    public void getUsers(String searchTerm, NetworkListCallback<User> callback) {
        networkingManager.searchForUsers(searchTerm, callback);
    }

    @Override
    public void getPredictions(String searchTerm, NetworkListCallback<Prediction> callback) {
        networkingManager.searchForPredictions(searchTerm, callback);
    }


    @Override
    public void onUserSelected(User user) {
        AnotherUsersProfileFragment fragment = new AnotherUsersProfileFragment(user.id);
        pushFragment(fragment);
    }

    @Override
    public void onPredictionSelected(Prediction prediction) {
        DetailsFragment fragment = new DetailsFragment(prediction);
        pushFragment(fragment);
    }
}

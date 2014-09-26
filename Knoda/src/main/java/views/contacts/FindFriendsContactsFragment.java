package views.contacts;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.Iterator;

import adapters.PagingAdapter;
import adapters.UserContactAdapter;
import butterknife.InjectView;
import models.ServerError;
import models.UserContact;
import networking.NetworkListCallback;
import views.core.BaseListFragment;

public class FindFriendsContactsFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<UserContact> {
    FindFriendsActivity parent;
    @InjectView(R.id.contacts_searchbar)
    EditText searchbar;
    UserContactAdapter adapter;
    boolean folllowedAll = false;

    @InjectView(R.id.search_view_clear)
    ImageView searchClear;

    public FindFriendsContactsFragment() {
    }

    public static FindFriendsContactsFragment newInstance(FindFriendsActivity parent) {
        FindFriendsContactsFragment fragment = new FindFriendsContactsFragment();
        fragment.parent = parent;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        parent.setOnBackPressedListener(new FindFriendsActivity.OnBackPressedListener() {
            @Override
            public void doBack() {
                if (searchbar.getText().length() > 0) {
                    searchbar.setText("");
                    searchbar.clearFocus();
                } else {
                    //show prompt
                    parent.onExit();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("FindFriendsContacts");
        pListView.setMode(PullToRefreshBase.Mode.DISABLED);
        searchbar.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        searchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                @Override
                                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                    if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                                                        //System.out.println(v.getText());
                                                        if (v.getText() != null) {
                                                            String searchterm = v.getText().toString();
                                                            adapter.searchFor(searchterm);
                                                        }
                                                        return true;
                                                    }
                                                    return false;

                                                }
                                            }

        );
        searchbar.addTextChangedListener(new TextWatcher() {
                                             @Override
                                             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                             }

                                             @Override
                                             public void onTextChanged(CharSequence s, int start, int before, int count) {

                                             }

                                             @Override
                                             public void afterTextChanged(Editable s) {
                                                 if (s.length() == 0) {
                                                     searchClear.setVisibility(View.INVISIBLE);
                                                     adapter.resetSearch();
                                                 } else if (s.length() > 0) {
                                                     searchClear.setVisibility(View.VISIBLE);
                                                 }
                                             }
                                         }

        );
        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbar.setText("");
                searchbar.clearFocus();
            }
        });
        searchClear.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public PagingAdapter getAdapter() {
        adapter = new UserContactAdapter(FindFriendsListCellHeader.CONTACTS, getActivity(), this, parent.networkingManager.getImageLoader(), parent);
        return adapter;
    }

    @Override
    public void getObjectsAfterObject(UserContact object,
                                      final NetworkListCallback<UserContact> callback) {
        if (parent.localContacts == null)
            return;
        parent.networkingManager.matchPhoneContacts(parent.localContacts, new NetworkListCallback<UserContact>() {
            @Override
            public void completionHandler(ArrayList<UserContact> object, ServerError error) {
                Iterator<UserContact> iterator = object.iterator();
                ArrayList<UserContact> toFollow = new ArrayList<UserContact>();
                while (iterator.hasNext()) {
                    UserContact u = iterator.next();
                    if (u.knodaInfo != null) {
                        toFollow.add(u);
                        iterator.remove();
                    }
                }
                for (int x = toFollow.size() - 1; x != -1; x--) {
                    object.add(0, toFollow.get(x));
                }
                adapter.followSize = toFollow.size();
                callback.completionHandler(object, error);
                if (object.size() == 0)
                    if (getView() != null)
                        getView().findViewById(R.id.contacts_search_container).setVisibility(View.GONE);

            }
        });
    }

    @Override
    public String noContentString() {
        return "There are no contacts";
    }

    @Override
    public void onLoadFinished() {
        if (!folllowedAll)
            if (adapter != null) {
                ((UserContactAdapter) adapter).followAll(true);
                folllowedAll = true;

            }
    }
}
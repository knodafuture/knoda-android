package views.contacts;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.knoda.knoda.R;

import adapters.PagingAdapter;
import adapters.UserContactAdapter;
import butterknife.InjectView;
import models.UserContact;
import networking.NetworkListCallback;
import views.core.BaseListFragment;

public class FindFriendsContactsFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<UserContact> {
    FindFriendsActivity parent;
    @InjectView(R.id.contacts_searchbar)
    EditText searchbar;

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
                if (event!=null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    System.out.println(v.getText());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public PagingAdapter getAdapter() {
        return new UserContactAdapter(FindFriendsListCellHeader.CONTACTS, getActivity(), this, parent.networkingManager.getImageLoader(), parent);
    }

    @Override
    public void getObjectsAfterObject(UserContact object, final NetworkListCallback<UserContact> callback) {
        if (parent.localContacts == null)
            return;
        parent.networkingManager.matchPhoneContacts(parent.localContacts, callback);
    }

    @Override
    public void onListViewCreated(ListView listView) {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //final Contest contest = (Contest) adapter.getItem(i - 1);
//                //i needs to be greater than list of known knoda friends +1 because of headers
//                final UserContact userContact = (UserContact) adapter.getItem(i - 2);
//                userContact.selected = true;
//                adapter.setItem(i - 2, userContact);
//                if (userContact.knodaInfo == null) {
//                    //if they have multiple forms of id
//                    ArrayList<String> contactTypes = new ArrayList<String>();
//                    if (userContact.emails != null)
//                        for (String s : userContact.emails) {
//                            contactTypes.add(s);
//                        }
//                    if (userContact.phones != null)
//                        for (String s : userContact.phones) {
//                            contactTypes.add(s);
//                        }
//
//                    if (contactTypes.size() > 1) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        final CharSequence[] array = contactTypes.toArray(new CharSequence[0]);
//                        builder.setTitle("Choose a contact method for " + userContact.contact_id);
//                        builder.setItems(array, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                addInvitation(array[which].toString());
//                            }
//                        });
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                        builder.show();
//                    } else if (contactTypes.size() == 1) {
//                        addInvitation(contactTypes.get(0));
//                    }
//                }
//            }
//        });


    }

    @Override
    public String noContentString() {
        return "There are no contacts";
    }

    @Override
    public void onLoadFinished() {
        ((UserContactAdapter) adapter).followAll(true);
    }


}
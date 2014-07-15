package views.group;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.knoda.knoda.R;

import java.util.ArrayList;

import adapters.InvitationsAdapter;
import adapters.InvitationsSearchAdapter;
import butterknife.InjectView;
import factories.GsonF;
import helpers.ContactsHelper;
import models.Contact;
import models.Group;
import models.GroupInvitation;
import models.InvitationHolder;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import views.core.BaseFragment;

public class InvitationsFragment extends BaseFragment implements InvitationsListCell.InvitationsCellCallbacks {

    private static ArrayList<Contact> contacts;
    @InjectView(R.id.invitations_search_edittext)
    EditText editText;
    @InjectView(R.id.invitations_listview)
    ListView listView;
    @InjectView(R.id.invitations_results_listview)
    ListView resultsListView;
    @InjectView(R.id.invitations_search_container)
    RelativeLayout searchContainer;
    long animationTime;
    private InvitationsAdapter invitationsAdapter;
    private InvitationsSearchAdapter searchAdapter;
    private InvitationHolder contextMenuHolder;
    private Group group;

    public static InvitationsFragment newInstance(Group group) {
        InvitationsFragment fragment = new InvitationsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP", GsonF.actory().toJson(group));
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density) / 1000);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density) / 1000);
        v.startAnimation(a);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
        bus.register(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit, menu);
        menu.removeGroup(R.id.default_menu_group);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_submit) {
            sendInvitations();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invitations, container, false);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("INVITE");
        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        animationTime = getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime);

        if (contacts == null) {
            spinner.show();
            new LoadContactsTask().execute();
        }

        searchAdapter = new InvitationsSearchAdapter(getActivity());
        invitationsAdapter = new InvitationsAdapter(getActivity(), this);

        resultsListView.setAdapter(searchAdapter);
        listView.setAdapter(invitationsAdapter);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    showSearchResults();
                else
                    hideSearchResults();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                handleSearchResultClick(i);
            }
        });
    }

    private void handleSearchResultClick(int position) {
        InvitationHolder holder = searchAdapter.getItem(position);

        if (holder.user != null)
            invitationsAdapter.addInvitation(holder);
        else {
            ArrayList<String> possibleValues = holder.contact.getContactMethods();
            if (possibleValues.size() == 1) {
                if (!holder.contact.phoneNumbers.isEmpty())
                    holder.selectedPhoneNumber = holder.contact.phoneNumbers.get(0);
                else if (!holder.contact.emailAddress.isEmpty())
                    holder.selectedEmail = holder.contact.emailAddress.get(0);
                if (holder.selectedEmail != null | holder.selectedPhoneNumber != null)
                    invitationsAdapter.addInvitation(holder);
            } else {
                contextMenuHolder = holder;
                registerForContextMenu(getView());
                getActivity().openContextMenu(getView());
            }
        }

        hideSearchResults();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Select a contact method");

        ArrayList<String> values = contextMenuHolder.contact.getContactMethods();
        int i = 0;
        for (String value : values) {
            menu.add(0, i, 0, value);
            i++;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();

        ArrayList<String> values = contextMenuHolder.contact.getContactMethods();
        if (id > values.size())
            return super.onContextItemSelected(item);

        String value = values.get(id);

        if (value.contains("@"))
            contextMenuHolder.selectedEmail = value;
        else
            contextMenuHolder.selectedPhoneNumber = value;

        invitationsAdapter.addInvitation(contextMenuHolder);
        contextMenuHolder = null;

        return true;
    }

    private void filter(String query) {

        networkingManager.autoCompleteUsers(query, new NetworkListCallback<User>() {
            @Override
            public void completionHandler(ArrayList<User> object, ServerError error) {
                searchAdapter.addKnodaUsers(object);
            }
        });

        ArrayList<Contact> filtered = new ArrayList<Contact>();

        for (Contact c : contacts) {

            if (c.name.toLowerCase().startsWith(query.toLowerCase())) {
                filtered.add(c);
                continue;
            }

            boolean phoneNumberMatch = false;
            for (String phoneNumber : c.phoneNumbers) {
                if (phoneNumber.toLowerCase().startsWith(query.toLowerCase())) {
                    filtered.add(c);
                    phoneNumberMatch = true;
                    continue;
                }
            }

            if (phoneNumberMatch)
                continue;

            boolean emailMatch = false;
            for (String email : c.emailAddress) {
                if (email.toLowerCase().startsWith(query.toLowerCase())) {
                    filtered.add(c);
                    emailMatch = true;
                    continue;
                }
            }
            if (emailMatch)
                continue;
        }

        searchAdapter.addContacts(filtered);

    }

    private void showSearchResults() {
        searchAdapter.addContacts(contacts);
        expand(resultsListView);
    }

    private void hideSearchResults() {
        collapse(resultsListView);
        hideKeyboard();
        editText.setText(null);
        editText.clearFocus();
    }

    @Override
    public void invitationRemovedAtPosition(int position) {
        invitationsAdapter.removeAtPosition(position);
    }

    private void sendInvitations() {
        if (invitationsAdapter.objects.size() == 0)
            return;

        spinner.show();
        ArrayList<GroupInvitation> invitations = new ArrayList<GroupInvitation>();

        for (InvitationHolder holder : invitationsAdapter.objects) {
            GroupInvitation invitation = new GroupInvitation();

            invitation.groupId = group.id;

            if (holder.user != null) {
                invitation.userId = holder.user.id;
            } else {
                if (holder.selectedEmail != null)
                    invitation.email = holder.selectedEmail;
                else if (holder.selectedPhoneNumber != null)
                    invitation.phoneNumber = holder.selectedPhoneNumber;
            }

            invitations.add(invitation);
        }

        networkingManager.sendInvitations(invitations, new NetworkCallback<GroupInvitation>() {
            @Override
            public void completionHandler(GroupInvitation object, ServerError error) {
                spinner.hide();
                if (error != null)
                    errorReporter.showError(error);
                else {
                    errorReporter.showError("Your invitations are on their way");
                    popFragment();
                }
            }
        });

    }

    private class LoadContactsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            contacts = ContactsHelper.getContacts(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            spinner.hide();
        }
    }
}

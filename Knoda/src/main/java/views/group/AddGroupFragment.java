package views.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.knoda.knoda.R;

import butterknife.InjectView;
import models.Group;
import models.ServerError;
import networking.NetworkCallback;
import pubsub.NewGroupEvent;
import views.core.BaseFragment;

public class AddGroupFragment extends BaseFragment {

    @InjectView(R.id.add_group_name_edittext)
    EditText nameEditText;

    @InjectView(R.id.add_group_description_edittext)
    EditText descriptionEditText;

    public static AddGroupFragment newInstance() {
        AddGroupFragment fragment = new AddGroupFragment();
        return fragment;
    }

    public AddGroupFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit, menu);
        menu.removeGroup(R.id.default_menu_group);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_submit)
            submitGroup();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_group, container, false);

        return view;
    }


    private void submitGroup() {

        hideKeyboard();
        if (!validate())
            return;

        Group group = new Group();

        group.name = nameEditText.getText().toString();
        group.description = descriptionEditText.getText().toString();

        spinner.show();

        networkingManager.submitGroup(group, new NetworkCallback<Group>() {
            @Override
            public void completionHandler(Group object, ServerError error) {
                spinner.hide();
                if (error != null) {
                    errorReporter.showError(error);
                } else {
                    bus.post(new NewGroupEvent(object));
                    popFragment();
                }
            }
        });

    }

    private boolean validate() {
        String errorMessage = null;
        if (errorMessage != null) {
            errorReporter.showError(errorMessage);
            return false;
        }
        return true;
    }
}

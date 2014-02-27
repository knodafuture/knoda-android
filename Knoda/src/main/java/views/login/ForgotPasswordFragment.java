package views.login;

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
import models.BaseModel;
import models.ForgotPasswordRequest;
import models.ServerError;
import networking.NetworkCallback;
import views.core.BaseFragment;

public class ForgotPasswordFragment extends BaseFragment {


    @InjectView(R.id.forgot_username_edittext)
    EditText editText;

    public static ForgotPasswordFragment newInstance() {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        return fragment;
    }
    public ForgotPasswordFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit, menu);
        menu.removeGroup(R.id.default_menu_group);
        getActivity().getActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_submit) {
            submit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void submit() {

        if (!validate())
            return;

        hideKeyboard();
        spinner.show();

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.login = editText.getText().toString();

        networkingManager.sendForgotPasswordRequest(request, new NetworkCallback<BaseModel>() {
            @Override
            public void completionHandler(BaseModel object, ServerError error) {
                spinner.hide();
                if (error != null) {
                    errorReporter.showError("We were unable to reset your password. Are you sure your email address has been registered on Knoda?");
                } else {
                    errorReporter.showError("A link to reset your password was sent to your email");
                    popFragment();
                }
            }
        });

    }

    public boolean validate() {
        if (editText.getText().length() == 0) {
            errorReporter.showError("Please enter your email address.");
            return false;
        }

        return true;
    }
}

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
import butterknife.OnClick;
import networking.NetworkCallback;
import helpers.EditTextDoneCallback;
import helpers.EditTextHelper;
import models.LoginRequest;
import models.ServerError;
import models.User;
import views.core.BaseFragment;
import views.core.MainActivity;

public class LoginFragment extends BaseFragment {

    @InjectView(R.id.login_username_edittext)
    EditText usernameField;
    @InjectView(R.id.login_password_edittext)
    EditText passwordField;


    @OnClick(R.id.login_forgot_button) void onForgotPassword() {
        ForgotPasswordFragment fragment = ForgotPasswordFragment.newInstance();
        pushFragment(fragment);
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getActionBar().show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.login, menu);
        menu.removeGroup(R.id.default_menu_group);
        getActivity().getActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_signin) {
            doLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        configureEditTextListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    private void configureEditTextListeners() {

        EditTextHelper.assignNextEditText(usernameField, passwordField);

        EditTextHelper.assignDoneListener(passwordField, new EditTextDoneCallback() {
            @Override
            public void onDone() {
                doLogin();
            }
        });

    }


    private void doLogin () {
        hideKeyboard();
        passwordField.clearFocus();
        usernameField.clearFocus();

        if (!validateFields())
            return;

        final LoginRequest request = new LoginRequest(usernameField.getText().toString(), passwordField.getText().toString());

        spinner.show();


        userManager.login(request, new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                spinner.hide();
                if (error != null)
                    errorReporter.showError(error);
                else
                    ((MainActivity)getActivity()).doLogin();
            }
        });

    }


    private boolean validateFields() {

        if (usernameField.getText().length() == 0) {
            errorReporter.showError("Username cannot be empty");
            usernameField.requestFocus();
            return false;
        } else if (passwordField.getText().length() == 0) {
            errorReporter.showError("Password cannot be empty");
            passwordField.requestFocus();
            return false;
        }

        return true;

    }
}

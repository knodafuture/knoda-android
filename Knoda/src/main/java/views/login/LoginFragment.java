package views.login;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import butterknife.InjectView;
import butterknife.OnClick;
import helpers.EditTextDoneCallback;
import helpers.EditTextHelper;
import models.LoginRequest;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import views.core.MainActivity;
import views.core.BaseDialogFragment;
import views.core.MainActivity;

public class LoginFragment extends BaseDialogFragment {

    @InjectView(R.id.login_username_edittext)
    EditText usernameField;
    @InjectView(R.id.login_password_edittext)
    EditText passwordField;


    @OnClick(R.id.login_forgot_button) void onForgotPassword() {
        ForgotPasswordFragment fragment = ForgotPasswordFragment.newInstance();
        fragment.show(getActivity().getFragmentManager(), "forgot");
    }

    @OnClick(R.id.login_button) void onLogin() {
        doLogin();
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
    public LoginFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureEditTextListeners();
        usernameField.requestFocus();
        showKeyboard(usernameField);
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
                if (error != null) {
                    errorReporter.showError("Invalid username or password");
                    return;
                }
                FlurryAgent.logEvent("LOGIN_EMAIL");
                ((MainActivity)getActivity()).doLogin();
                dismiss();
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

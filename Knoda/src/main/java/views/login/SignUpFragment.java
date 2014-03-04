package views.login;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
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
import helpers.EditTextDoneCallback;
import helpers.EditTextHelper;
import helpers.PasswordValidator;
import managers.NetworkingManager;
import models.ServerError;
import models.SignUpRequest;
import models.User;
import networking.NetworkCallback;
import views.core.BaseFragment;
import views.core.MainActivity;

public class SignUpFragment extends BaseFragment {
    @InjectView(R.id.signup_email_edittext)
    EditText emailField;

    @InjectView(R.id.signup_password_edittext)
    EditText passwordField;

    @InjectView(R.id.signup_username_edittext)
    EditText usernameField;

    @OnClick(R.id.signup_terms_button) void onTerms() {openUrl(NetworkingManager.termsOfServiceUrl);}

    @OnClick(R.id.signup_privacy_button) void onPP() {openUrl(NetworkingManager.privacyPolicyUrl);}

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        return fragment;
    }
    public SignUpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getActionBar().show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        emailField.requestFocus();
        showKeyboard(emailField);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        usernameField.setFilters(new InputFilter[]{filter});
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.signup, menu);
        menu.removeGroup(R.id.default_menu_group);
        setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_signup) {
            doSignup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setupListeners() {

        EditTextHelper.assignNextEditText(emailField, usernameField);
        EditTextHelper.assignNextEditText(usernameField, passwordField);
        EditTextHelper.assignDoneListener(passwordField, new EditTextDoneCallback() {
            @Override
            public void onDone() {
                doSignup();
            }
        });
    }

    public void doSignup() {
        hideKeyboard();
        emailField.clearFocus();
        usernameField.clearFocus();
        passwordField.clearFocus();



        if (!validateFields())
            return;


        spinner.show();

        final SignUpRequest request = new SignUpRequest(emailField.getText().toString(), usernameField.getText().toString(), passwordField.getText().toString());

        userManager.signup(request, new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                spinner.hide();
                if (error != null)
                    errorReporter.showError(error);
                else {
                    sharedPrefManager.setFirstLaunch(true);
                    ((MainActivity)getActivity()).doLogin();
                }
            }
        });



    }

    public void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    private boolean validateFields() {

        if (emailField.getText().toString().length() == 0) {
            errorReporter.showError("Email cannot be empty");
            return false;
        } else if (usernameField.getText().toString().length() == 0) {
            errorReporter.showError("Username cannot be empty");
            return false;
        } else if (!PasswordValidator.validate(passwordField.getText().toString())) {
            errorReporter.showError("Password must be between 6 and 20 characters");
            return false;
        }

        return true;

    }

}

package views.login;


import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.knoda.knoda.R;

import butterknife.InjectView;
import models.LoginRequest;
import models.LoginResponse;
import models.ServerError;
import networking.NetworkCallback;
import views.core.BaseFragment;
import views.core.MainActivity;

public class LoginFragment extends BaseFragment {

    @InjectView(R.id.login_username_edittext)
    EditText usernameField;
    @InjectView(R.id.login_password_edittext)
    EditText passwordField;

    boolean didLogin = false;

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
        getActivity().getActionBar().setTitle("");
        super.onCreateOptionsMenu(menu, inflater);
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
    public void onPause() {
        super.onPause();

        if (!didLogin)
            getActivity().getActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameField = (EditText)view.findViewById(R.id.login_username_edittext);
        passwordField = (EditText)view.findViewById(R.id.login_password_edittext);

        configureEditTextListeners();

        return view;
    }

    private void configureEditTextListeners() {

        TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    passwordField.requestFocus();
                } else if (i == EditorInfo.IME_ACTION_DONE) {
                    doLogin();
                }

                return true;
            }
        };

        usernameField.setOnEditorActionListener(listener);
        passwordField.setOnEditorActionListener(listener);
    }

    private void doLogin () {

        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        passwordField.clearFocus();
        usernameField.clearFocus();


        if (!validateFields())
            return;

        final LoginRequest request = new LoginRequest(usernameField.getText().toString(), passwordField.getText().toString());

        spinner.show();


        mNetworkingManager.login(request, new NetworkCallback<LoginResponse>() {
            @Override
            public void completionHandler(LoginResponse object, ServerError error) {
                spinner.hide();
                if (error != null)
                    errorReporter.showError(error);
                else {
                    didLogin = true;
                    ((MainActivity)getActivity()).doLogin(request, object);
                }
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

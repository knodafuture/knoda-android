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

import com.android.volley.VolleyError;
import com.knoda.knoda.R;

import javax.inject.Inject;

import core.ErrorReporter;
import models.LoginRequest;
import models.LoginResponse;
import networking.NetworkCallback;
import networking.NetworkingManager;
import views.core.BaseFragment;
import views.core.MainActivity;
import views.core.Spinner;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginFragment extends BaseFragment {

    EditText usernameField;
    EditText passwordField;

    @Inject
    NetworkingManager mNetworkingManager;

    @Inject
    Spinner spinner;

    @Inject
    ErrorReporter reporter;

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

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).inject(this);
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

        LoginRequest request = new LoginRequest(usernameField.getText().toString(), passwordField.getText().toString());

        spinner.show();


        mNetworkingManager.login(request, new NetworkCallback<LoginResponse>() {
            @Override
            public void completionHandler(LoginResponse object, VolleyError error) {

            }
        });


        didLogin = true;


    }


    private boolean validateFields() {

        if (usernameField.getText().length() == 0) {
            reporter.showError("Username cannot be empty");
            usernameField.requestFocus();
            return false;
        } else if (passwordField.getText().length() == 0) {
            reporter.showError("Password cannot be empty");
            passwordField.requestFocus();
            return false;
        }

        return true;

    }
}

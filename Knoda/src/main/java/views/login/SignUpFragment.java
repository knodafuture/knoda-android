package views.login;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;
import com.tapjoy.TapjoyConnect;

import org.joda.time.DateTime;

import butterknife.InjectView;
import butterknife.OnClick;
import helpers.EditTextDoneCallback;
import helpers.EditTextHelper;
import helpers.PasswordValidator;
import helpers.TapjoyPPA;
import models.ServerError;
import models.SignUpRequest;
import models.SocialAccount;
import models.User;
import networking.NetworkCallback;
import views.avatar.UserAvatarChooserFragment;
import views.core.BaseDialogFragment;
import views.core.MainActivity;

public class SignUpFragment extends BaseDialogFragment {
    @InjectView(R.id.signup_email_edittext)
    EditText emailField;

    @InjectView(R.id.signup_password_edittext)
    EditText passwordField;

    @InjectView(R.id.signup_username_edittext)
    EditText usernameField;

    @OnClick(R.id.welcome_login_facebook) void onFB() {doFacebookLogin();}

    @OnClick(R.id.welcome_login_twitter) void onTwitter() {doTwitterLogin();}

    @OnClick(R.id.signup_button) void onSignup() {doSignup();}

    @OnClick(R.id.signup_close) void onSignupClose() {dismissFade();}

    @OnClick(R.id.welcome_already_user) void onSignIn() {
        LoginFragment f = LoginFragment.newInstance();
        f.show(getFragmentManager(), "login");
        dismissFade();
    }

    private static final int avatarResultCode = 123988123;

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
        View inflate =inflater.inflate(R.layout.fragment_sign_up, container, false);
        updateBackground();
        return inflate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        InputFilter[] filterArray = new InputFilter[2];
        filterArray[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        filterArray[1] = new InputFilter.LengthFilter(15);
        usernameField.setFilters(filterArray);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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

    public void finish(boolean shouldConfirm) {
        dismiss();
        ((MainActivity)getActivity()).doLogin();
        SignupConfirmFragment f = SignupConfirmFragment.newInstance();

        if (shouldConfirm)
            f.show(getActivity().getFragmentManager(), "confirm");
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
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
                    TapjoyConnect.getTapjoyConnectInstance().actionComplete(TapjoyPPA.TJC_SIGN_UP_FOR_KNODA___ANDROID_PPE);
                    sharedPrefManager.setFirstLaunch(true);
                    FlurryAgent.logEvent("SIGNUP_EMAIL");
                    UserAvatarChooserFragment f = new UserAvatarChooserFragment();
                    dismiss();
                    f.show(getActivity().getFragmentManager(), "avatar");
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

    public void doFacebookLogin() {
        spinner.show();
        facebookManager.openSession(getActivity(), new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    spinner.hide();
                    errorReporter.showError(error);
                    return;
                }

                userManager.socialSignIn(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                        } else {
                            DateTime curTime = new DateTime();
                            DateTime newTime = curTime.minusMinutes(1);
                            int i = (int) (newTime.getMillis()/1000);
                            int j = (int) (userManager.user.created_at.getMillis()/1000);
                            if(i <= j) {
                                FlurryAgent.logEvent("SIGNUP_FACEBOOK");
                                finish(true);
                            } else {
                                FlurryAgent.logEvent("LOGIN_FACEBOOK");
                                finish(false);
                            }
                        }
                    }
                });
            }
        });
    }

    public void doTwitterLogin() {

        if (twitterManager.hasAuthInfo()) {
            finishTwitterLogin();
            return;
        }

        spinner.show();

        WelcomeFragment.requestingTwitterLogin = true;

        twitterManager.openSession(getActivity());
    }

    public void finishTwitterLogin() {
        spinner.show();
        twitterManager.getSocialAccount(new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    errorReporter.showError(error);
                    spinner.hide();
                    return;
                }

                userManager.socialSignIn(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }

                        DateTime curTime = new DateTime();
                        DateTime newTime = curTime.minusMinutes(1);
                        int i = (int) (newTime.getMillis()/1000);
                        int j = (int) (userManager.user.created_at.getMillis()/1000);
                        if(j >= i) {
                            FlurryAgent.logEvent("SIGNUP_TWITTER");
                            finish(true);
                        } else {
                            FlurryAgent.logEvent("LOGIN_TWITTER");
                            finish(false);
                        }
                    }
                });
            }
        });

    }
}
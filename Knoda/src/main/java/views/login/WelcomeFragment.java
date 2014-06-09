package views.login;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import org.joda.time.DateTime;

import butterknife.OnClick;
import managers.NetworkingManager;
import models.ServerError;
import models.SocialAccount;
import models.User;
import networking.NetworkCallback;
import views.core.BaseFragment;
import views.core.MainActivity;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class WelcomeFragment extends BaseFragment {

    @OnClick(R.id.welcome_login_email) void onLogin() {
        LoginFragment fragment = LoginFragment.newInstance();
        pushFragment(fragment);
    }

    @OnClick(R.id.welcome_signup_email) void onSignup() {
        SignUpFragment fragment = SignUpFragment.newInstance();
        pushFragment(fragment);
    }

    @OnClick(R.id.signup_terms_button) void onTerms() {openUrl(NetworkingManager.termsOfServiceUrl);}

    @OnClick(R.id.signup_privacy_button) void onPP() {openUrl(NetworkingManager.privacyPolicyUrl);}

    @OnClick(R.id.welcome_login_facebook) void onFB() {doFacebookLogin();}

    @OnClick(R.id.welcome_login_twitter) void onTwitter() {doTwitterLogin();}


    static boolean requestingTwitterLogin;

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }
    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        FlurryAgent.logEvent("LANDING");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeGroup(R.id.default_menu_group);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().hide();
        if (requestingTwitterLogin) {
            if (twitterManager.hasAuthInfo())
                finishTwitterLogin();
            else
                errorReporter.showError("Error authorizing with Twitter. Please try again later.");
        }
        requestingTwitterLogin = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                            ((MainActivity)getActivity()).doLogin();
                            DateTime curTime = new DateTime();
                            DateTime newTime = curTime.minusMinutes(1);
			    int i = (int) (newTime.getMillis()/1000);
                            int j = (int) (userManager.user.created_at.getMillis()/1000);
                            if(i <= j) {
                                FlurryAgent.logEvent("SIGNUP_FACEBOOK");
                            } else {
                                FlurryAgent.logEvent("LOGIN_FACEBOOK");
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
            requestingTwitterLogin = false;
            return;
        }

        requestingTwitterLogin = true;
        spinner.show();
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

                        ((MainActivity)getActivity()).doLogin();
                        DateTime curTime = new DateTime();
                        DateTime newTime = curTime.minusMinutes(1);
                        int i = (int) (newTime.getMillis()/1000);
                        int j = (int) (userManager.user.created_at.getMillis()/1000);
                        if(j >= i) {
                            FlurryAgent.logEvent("SIGNUP_TWITTER");
                        } else {
                            FlurryAgent.logEvent("LOGIN_TWITTER");
                        }

                    }
                });
            }
        });

    }
    public void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}

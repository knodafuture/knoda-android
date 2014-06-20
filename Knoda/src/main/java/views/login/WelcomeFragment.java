package views.login;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import butterknife.OnClick;
import managers.NetworkingManager;
import models.ServerError;
import models.SocialAccount;
import models.User;
import networking.NetworkCallback;
import pubsub.ScreenCaptureEvent;
import views.core.BaseDialogFragment;
import views.core.MainActivity;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends BaseDialogFragment {
    @OnClick(R.id.signup_terms_button)
    void onTerms() {
        openUrl(NetworkingManager.termsOfServiceUrl);
    }

    @OnClick(R.id.signup_privacy_button)
    void onPP() {
        openUrl(NetworkingManager.privacyPolicyUrl);
    }

    @OnClick(R.id.welcome_login_facebook)
    void onFB() {
        doFacebookLogin();
    }

    @OnClick(R.id.welcome_login_twitter)
    void onTwitter() {
        doTwitterLogin();
    }

    @OnClick(R.id.wall_signup_button)
    void onSignUp() {
        SignUpFragment f = SignUpFragment.newInstance();
        f.show(getFragmentManager(), "signup");
        dismissFade();
    }

    @OnClick(R.id.wall_close)
    void onClose() {
        cancel();
    }

    @OnClick(R.id.wall_later)
    void onLater() {
        cancel();
    }

    @OnClick(R.id.wall_login)
    void onLogin() {
        LoginFragment f = LoginFragment.newInstance();
        f.show(getActivity().getFragmentManager(), "login");
        dismissFade();
    }

    String wtext = "";
    String wprompt = "";

    public static boolean requestingTwitterLogin;

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Subscribe
    public void screenCapture(final ScreenCaptureEvent event) {
        updateBackground();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.logEvent("LANDING");
        bus.register(this);
        Bundle b = this.getArguments();
        if (b == null)
            return;
        if (b.getCharSequence("welcometext") != null)
            wtext = b.getCharSequence("welcometext").toString();
        if (b.getCharSequence("welcomeprompt") != null)
            wprompt = b.getCharSequence("welcomeprompt").toString();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestingTwitterLogin) {
            if (twitterManager.hasAuthInfo())
                finishTwitterLogin();
            else
                errorReporter.showError("Error authorizing with Twitter. Please try again later.");
        }
        requestingTwitterLogin = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        TextView welcomeText=((TextView)view.findViewById(R.id.wall_welcome));
        TextView welcomePrompt=((TextView)view.findViewById(R.id.wall_prompt));
        if (!wtext.equals(""))
            welcomeText.setText(wtext);
        if(!wprompt.equals(""))
            welcomePrompt.setText(wprompt);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
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
                            finish();
                            DateTime curTime = new DateTime();
                            DateTime newTime = curTime.minusMinutes(1);
                            int i = (int) (newTime.getMillis() / 1000);
                            int j = (int) (userManager.user.created_at.getMillis() / 1000);
                            if (i <= j) {
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
                        finish();

                        DateTime curTime = new DateTime();
                        DateTime newTime = curTime.minusMinutes(1);
                        int i = (int) (newTime.getMillis() / 1000);
                        int j = (int) (userManager.user.created_at.getMillis() / 1000);
                        if (j >= i) {
                            FlurryAgent.logEvent("SIGNUP_TWITTER");
                        } else {
                            FlurryAgent.logEvent("LOGIN_TWITTER");
                        }
                    }
                });
            }
        });

    }

    public void finish() {
        dismiss();
        ((MainActivity) getActivity()).doLogin();
        SignupConfirmFragment f = SignupConfirmFragment.newInstance();
        f.show(getActivity().getFragmentManager(), "confirm");
    }
}

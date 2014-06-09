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

import butterknife.OnClick;
import models.ServerError;
import models.SocialAccount;
import networking.NetworkCallback;
import views.core.BaseDialogFragment;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class WelcomeFragment extends BaseDialogFragment {

//    @OnClick(R.id.welcome_login_email) void onLogin() {
//        LoginFragment fragment = LoginFragment.newInstance();
//        pushFragment(fragment);
//    }
//
//    @OnClick(R.id.welcome_signup_email) void onSignup() {
//        SignUpFragment fragment = SignUpFragment.newInstance();
//        pushFragment(fragment);
//    }
//
//    @OnClick(R.id.signup_terms_button) void onTerms() {openUrl(NetworkingManager.termsOfServiceUrl);}
//
//    @OnClick(R.id.signup_privacy_button) void onPP() {openUrl(NetworkingManager.privacyPolicyUrl);}
//
//    @OnClick(R.id.welcome_login_facebook) void onFB() {doFacebookLogin();}
//
//    @OnClick(R.id.welcome_login_twitter) void onTwitter() {doTwitterLogin();}


    @OnClick(R.id.wall_signin_button) void onSignIn() {
        dismiss();

        LoginFragment f = LoginFragment.newInstance();
        f.show(getFragmentManager(), "login");
    }

    @OnClick(R.id.wall_signup_button) void onSignUp() {
        dismiss();

        SignUpFragment f = SignUpFragment.newInstance();
        f.show(getFragmentManager(), "signup");
    }
    @OnClick(R.id.wall_close) void onClose() {
        dismiss();
    }

    @OnClick(R.id.wall_later) void onLater() {
        dismiss();
    }

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
        FlurryAgent.logEvent("LANDING");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
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
}

package views.settings;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;
import com.squareup.otto.Bus;

import managers.FacebookManager;
import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.TwitterManager;
import managers.UserManager;
import models.KnodaScreen;
import models.PasswordChangeRequest;
import models.ServerError;
import models.SocialAccount;
import models.User;
import networking.NetworkCallback;
import unsorted.ErrorReporter;
import unsorted.Logger;
import views.avatar.UserAvatarChooserActivity;
import views.core.MainActivity;
import views.core.Spinner;

public class SettingsProfileFragment extends PreferenceFragment {

    private PreferenceScreen preferenceScreen;
    BitmapDrawable userPic;

    public NetworkingManager networkingManager;

    public Spinner spinner;

    public ErrorReporter errorReporter;

    public UserManager userManager;
    public Bus bus;

    public SharedPrefManager sharedPrefManager;

    public FacebookManager facebookManager;

    public TwitterManager twitterManager;

    Preference.OnPreferenceClickListener clickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("photo")) {
                Intent intent = new Intent(getActivity(), UserAvatarChooserActivity.class);
                intent.putExtra("cancelable", true);
                startActivityForResult(intent, 123123129);
            } else if (preference.getKey().equals("username")) {

            } else if (preference.getKey().equals("email")) {

            } else if (preference.getKey().equals("password")) {
                changePassword();
            } else if (preference.getKey().equals("facebook")) {
                handleFB();
            } else if (preference.getKey().equals("twitter")) {
                handleTwitter();
            }
            return false;
        }
    };

    public static SettingsProfileFragment newInstance() {
        SettingsProfileFragment fragment = new SettingsProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.layout.fragment_settings);
        getActivity().invalidateOptionsMenu();
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        MainActivity mainActivity = ((MainActivity) getActivity());
        preferenceScreen = this.getPreferenceScreen();
        userManager = ((MainActivity) getActivity()).userManager;
        networkingManager = mainActivity.networkingManager;
        spinner = mainActivity.spinner;
        facebookManager = new FacebookManager(userManager, networkingManager);
        twitterManager = mainActivity.twitterManager;
        errorReporter = mainActivity.errorReporter;

        userPic = (BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.ic_notification_avatar);
        ((MainActivity) getActivity()).networkingManager.getImageLoader().get(userManager.getUser().avatar.big, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                userPic = new BitmapDrawable(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    public void buildPage() {
        preferenceScreen.removeAll();
        userPic = (BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.ic_notification_avatar);

        Context c = getActivity();
        Preference p1 = new Preference(c);
        p1.setTitle("Profile Photo");
        p1.setSummary("Keep it fresh, tap to update your mugshot");
        p1.setKey("photo");
        p1.setIcon(userPic);
        p1.setOnPreferenceClickListener(clickListener);
        preferenceScreen.addPreference(p1);

        Preference p2 = new Preference(c);
        p2.setTitle(userManager.getUser().username);
        p2.setSummary("username");
        p2.setKey("username");
        p2.setOnPreferenceClickListener(clickListener);
        preferenceScreen.addPreference(p2);

        Preference p3 = new Preference(c);
        p3.setTitle(userManager.getUser().email);
        p3.setSummary("email");
        p3.setKey("email");
        p3.setOnPreferenceClickListener(clickListener);
        preferenceScreen.addPreference(p3);

        Preference p4 = new Preference(c);
        p4.setTitle("Change Password");
        p4.setSummary("lock it down");
        p4.setKey("password");
        p4.setOnPreferenceClickListener(clickListener);
        preferenceScreen.addPreference(p4);

        Preference p5 = new Preference(c);
        if (userManager.getUser().getFacebookAccount() != null) {
            p5.setTitle(userManager.getUser().getFacebookAccount().providerAccountName);
            p5.setIcon(R.drawable.facebook_share_active);
        } else {
            p5.setTitle("Connect to Facebook");
            p5.setIcon(R.drawable.facebook_share);
        }
        p5.setSummary("Facebook");
        p5.setKey("facebook");
        p5.setOnPreferenceClickListener(clickListener);
        preferenceScreen.addPreference(p5);

        Preference p6 = new Preference(c);
        if (userManager.getUser().getTwitterAccount() != null) {
            p6.setTitle("@" + userManager.getUser().getTwitterAccount().providerAccountName);
            p6.setIcon(R.drawable.twitter_share_active);
        } else {
            p6.setTitle("Connect to Twitter");
            p6.setIcon(R.drawable.twitter_share);
        }

        p6.setSummary("Twitter");
        p6.setKey("twitter");
        p6.setOnPreferenceClickListener(clickListener);
        preferenceScreen.addPreference(p6);

        ((MainActivity) getActivity()).networkingManager.getImageLoader().get(userManager.getUser().avatar.big, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                userPic = new BitmapDrawable(response.getBitmap());
                preferenceScreen.getPreference(0).setIcon(userPic);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        buildPage();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.global, menu);
        menu.removeItem(R.id.action_search);
        menu.removeItem(R.id.action_add_prediction);
        ((MainActivity) getActivity()).setActionBarTitle("PROFILE SETTINGS");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void handleFB() {
        if (userManager.getUser().getFacebookAccount() != null)
            removeFBAccount();
        else
            addFBAccount();
    }

    private void addFBAccount() {
        spinner.show();
        facebookManager.openSession(getActivity(), new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    spinner.hide();
                    errorReporter.showError(error);
                    return;
                }

                userManager.addSocialAccount(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User user, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }
                        updateUser(user);
                    }
                });
            }
        });
    }

    private void removeFBAccount() {
        if (userManager.getUser().email == null && userManager.getUser().getTwitterAccount() == null) {
            errorReporter.showError("You must enter an email address before removing your last social account, or your account will be lost forever.");
            return;
        }

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setTitle("Are you sure you wish to remove your Facebook account?")
                .create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                spinner.show();
                userManager.deleteSocialAccount(userManager.getUser().getFacebookAccount(), new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }
                        updateUser(object);
                    }
                });
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

    private void handleTwitter() {
        if (userManager.getUser().getTwitterAccount() != null)
            removeTwitterAccount();
        else
            addTwitterAccount();
    }

    private void addTwitterAccount() {
        if (twitterManager.hasAuthInfo()) {
            finishAddingTwitterAccount();
        }

        //requestingTwitterInfo = true;
        ((MainActivity) getActivity()).requestStartupScreen(KnodaScreen.KnodaScreenOrder.PROFILE);
        spinner.show();
        twitterManager.openSession(getActivity());
    }

    private void finishAddingTwitterAccount() {
        Logger.log("FINISHING TWITTER -------");
        spinner.show();
        twitterManager.getSocialAccount(new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    errorReporter.showError(error);
                    spinner.hide();
                    return;
                }

                userManager.addSocialAccount(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User user, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }
                        updateUser(user);
                    }
                });
            }
        });
    }

    private void removeTwitterAccount() {
        if (userManager.getUser().email == null && userManager.getUser().getFacebookAccount() == null) {
            errorReporter.showError("You must enter an email address before removing your last social account, or your account will be lost forever.");
            return;
        }

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setTitle("Are you sure you wish to remove your Twitter account?")
                .create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                spinner.show();
                userManager.deleteSocialAccount(userManager.getUser().getTwitterAccount(), new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }
                        updateUser(object);
                    }
                });
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

    private void updateUser(User user) {

        if (user == null)
            return;

        userManager.refreshUser(new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                buildPage();
            }
        });
    }

    private boolean validateChangePassword(final View changePasswordView) {
        EditText currentPassword = (EditText) changePasswordView.findViewById(R.id.current_password);
        EditText newPassword = (EditText) changePasswordView.findViewById(R.id.new_password);
        EditText newPasswordConfirm = (EditText) changePasswordView.findViewById(R.id.new_password_confirm);
        if (newPassword.getText().toString().length() < 6) {
            errorReporter.showError("Minimum password length is 6 characters");
            return false;
        }
        if (!newPassword.getText().toString().equals(newPasswordConfirm.getText().toString())) {
            errorReporter.showError("Passwords do not match.");
            return false;
        }
        return true;
    }


    private View.OnClickListener changePassword(final View changePasswordView, final AlertDialog dialog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                PasswordChangeRequest pcr = new PasswordChangeRequest();
                EditText currentPassword = (EditText) changePasswordView.findViewById(R.id.current_password);
                EditText newPassword = (EditText) changePasswordView.findViewById(R.id.new_password);
                EditText newPasswordConfirm = (EditText) changePasswordView.findViewById(R.id.new_password_confirm);
                if (validateChangePassword(changePasswordView)) {
                    pcr.currentPassword = currentPassword.getText().toString();
                    pcr.newPassword = newPassword.getText().toString();
                    networkingManager.changePassword(pcr, new NetworkCallback<User>() {
                        @Override
                        public void completionHandler(User u, ServerError error) {
                            if (error == null) {
                                dialog.dismiss();
                                //hideKeyboard();
                            } else {
                                errorReporter.showError(error);
                            }
                        }
                    });
                } else {
                    return;
                }
            }
        };
    }

    private void changePassword() {
        final View changePasswordView = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .setView(changePasswordView)
                .setTitle("Change Your Password")
                .create();

        alert.show();
        EditText currentPassword = (EditText) changePasswordView.findViewById(R.id.current_password);
        currentPassword.requestFocus();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(changePassword(changePasswordView, alert));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
                //hideKeyboard();
            }
        });

    }


}

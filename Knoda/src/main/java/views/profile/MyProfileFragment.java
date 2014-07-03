package views.profile;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import models.KnodaScreen;
import models.PasswordChangeRequest;
import models.ServerError;
import models.SettingsCategory;
import models.SocialAccount;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import unsorted.Logger;
import views.avatar.UserAvatarChooserActivity;
import views.core.BaseFragment;
import views.core.MainActivity;

public class MyProfileFragment extends BaseFragment {
    private static final int PHOTO_RESULT_CODE = 123123129;
    private static boolean requestingTwitterInfo;
    @InjectView(R.id.profile_username_edittext)
    TextView username;
    @InjectView(R.id.profile_email_edittext)
    TextView email;
    @InjectView(R.id.profile_view_user_header)
    UserProfileHeaderView header;
    @InjectView(R.id.button_sign_out)
    Button signOutButton;
    @InjectView(R.id.profile_facebook_account_name)
    TextView facebookAccountNameTextView;
    @InjectView(R.id.profile_facebook_imageview)
    ImageView facebookImageView;
    @InjectView(R.id.profile_twitter_account_name)
    TextView twitterAccountNameTextView;
    @InjectView(R.id.profile_twitter_imageview)
    ImageView twitterImageView;

    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    @OnClick(R.id.profile_facebook_button)
    void onFB() {
        handleFB();
    }

    @OnClick(R.id.profile_twitter_button)
    void onTwitter() {
        handleTwitter();
    }

    @OnClick(R.id.user_profile_header_avatar)
    void onClickAvatar() {
        getActivity().findViewById(R.id.user_profile_header_avatar).setEnabled(false);
        Intent intent = new Intent(getActivity(), UserAvatarChooserActivity.class);
        intent.putExtra("cancelable", true);
        startActivityForResult(intent, PHOTO_RESULT_CODE);
    }

    @OnClick(R.id.button_sign_out)
    void onClickSignOut() {
        signOutButton.setEnabled(false);
        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setTitle("Are you sure you wish to sign out?")
                .create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                userManager.signout(new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User u, ServerError error) {
                        alert.dismiss();
                        ((MainActivity) getActivity()).restart();
                    }
                });
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signOutButton.setEnabled(true);
                alert.dismiss();
            }
        });

    }

    @OnClick(R.id.profile_username_edittext)
    void onClickUsername() {
        LayoutInflater li = getActivity().getLayoutInflater();
        final View changeUsernameView = li.inflate(R.layout.dialog_change_username, null);
        EditText username = (EditText) changeUsernameView.findViewById(R.id.username);
        username.setText("");
        username.append(userManager.getUser().username);
        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .setView(changeUsernameView)
                .setTitle("Change Your Username")
                .create();
        alert.show();
        username.requestFocus();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(changeUsername(changeUsernameView, alert));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
                hideKeyboard();
            }
        });
    }

    @OnClick(R.id.profile_email_edittext)
    void onClickEmail() {
        LayoutInflater li = getActivity().getLayoutInflater();
        final View changeEmailView = li.inflate(R.layout.dialog_change_email, null);
        EditText email = (EditText) changeEmailView.findViewById(R.id.email);
        if (userManager.getUser().email != null)
            email.setText(userManager.getUser().email);
        else
            email.setText("");
        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .setView(changeEmailView)
                .setTitle("Change Your Email")
                .create();
        alert.show();
        email.requestFocus();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(changeEmail(changeEmailView, alert));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
                hideKeyboard();
            }
        });
    }

    @OnClick(R.id.profile_password_edittext)
    void onClickPassword() {
        LayoutInflater li = getActivity().getLayoutInflater();
        final View changePasswordView = li.inflate(R.layout.dialog_change_password, null);
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
                hideKeyboard();
            }
        });
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).networkingManager.getSettings(new NetworkListCallback<SettingsCategory>() {
            @Override
            public void completionHandler(ArrayList<SettingsCategory> object, ServerError error) {
                for (SettingsCategory s : object) {
                    ((MainActivity) getActivity()).settings.put(s.name, s.settings);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myprofile, container, false);
        ButterKnife.inject(this, view);
        getActivity().invalidateOptionsMenu();

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final User user = userManager.getUser();
        updateUser(user);
        FlurryAgent.logEvent("Profile_Screen");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.user_profile_header_avatar).setEnabled(true);

        if (requestingTwitterInfo) {
            if (twitterManager.hasAuthInfo())
                finishAddingTwitterAccount();
            else
                errorReporter.showError("Error authorizing with Twitter. Please try again later.");
        }
        requestingTwitterInfo = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        String avatarPath = data.getExtras().getString(MediaStore.EXTRA_OUTPUT);

        Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
        header.avatarImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_search);
        menu.removeItem(R.id.action_add_prediction);
        if (((MainActivity) getActivity()).currentFragment.equals(this.getClass().getSimpleName()) && menu.findItem(R.id.action_settings) != null)
            menu.findItem(R.id.action_settings).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        //if (itemId == R.id.action_settings)

        return super.onOptionsItemSelected(item);

    }

    private void updateUser(User user) {
        if (user == null)
            return;

        setTitle(user.username.toUpperCase());
        username.setText(user.username);
        if (user.email == null) {
            email.setText("Add your email address");
        } else
            email.setText(user.email);
        header.setUser(user);
        if (user.avatar != null)
            header.avatarImageView.setImageUrl(user.avatar.big, networkingManager.getImageLoader());

        if (user.getFacebookAccount() != null) {
            facebookAccountNameTextView.setText(user.getFacebookAccount().providerAccountName);
            facebookImageView.setImageResource(R.drawable.facebook_share_active);
        } else {
            facebookAccountNameTextView.setText("Connect to Facebook");
            facebookImageView.setImageResource(R.drawable.facebook_share);
        }

        if (user.getTwitterAccount() != null) {
            twitterAccountNameTextView.setText("@" + user.getTwitterAccount().providerAccountName);
            twitterImageView.setImageResource(R.drawable.twitter_share_active);
        } else {
            twitterAccountNameTextView.setText("Connect to Twitter");
            twitterImageView.setImageResource(R.drawable.twitter_share);
        }
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
                                hideKeyboard();
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

    private View.OnClickListener changeEmail(final View changeEmailView, final AlertDialog dialog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                EditText email = (EditText) changeEmailView.findViewById(R.id.email);
                final User user = new User();
                user.email = email.getText().toString();
                userManager.updateUser(user, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User u, ServerError error) {
                        if (error == null) {
                            updateUser(u);
                            dialog.dismiss();
                            hideKeyboard();
                        } else {
                            errorReporter.showError(error);
                        }
                    }
                });
            }
        };
    }

    private View.OnClickListener changeUsername(final View changeUsernameView, final AlertDialog dialog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                EditText username = (EditText) changeUsernameView.findViewById(R.id.username);
                final User user = new User();
                user.username = username.getText().toString();
                userManager.updateUser(user, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User u, ServerError error) {
                        if (error == null) {
                            updateUser(u);
                            dialog.dismiss();
                            hideKeyboard();
                        } else {
                            errorReporter.showError(error);
                        }
                    }
                });
            }
        };
    }

    private boolean validateChangePassword(final View changePasswordView) {
        EditText currentPassword = (EditText) changePasswordView.findViewById(R.id.current_password);
        EditText newPassword = (EditText) changePasswordView.findViewById(R.id.new_password);
        EditText newPasswordConfirm = (EditText) changePasswordView.findViewById(R.id.new_password_confirm);
        if (newPassword.getText().toString().length() < 6) {
            errorReporter.showError("Minimum password length is 6 characters");
        }
        if (!newPassword.getText().toString().equals(newPasswordConfirm.getText().toString())) {
            errorReporter.showError("Passwords do not match.");
            return false;
        }
        return true;
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

        requestingTwitterInfo = true;
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

}

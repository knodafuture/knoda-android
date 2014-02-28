package views.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import models.PasswordChangeRequest;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import views.core.BaseFragment;
import views.core.MainActivity;
import views.login.PhotoChooserActivity;

public class MyProfileFragment extends BaseFragment {
    @InjectView(R.id.profile_username_edittext)
    TextView username;
    @InjectView(R.id.profile_email_edittext)
    TextView email;
    @InjectView(R.id.profile_view_user_header)
    UserProfileHeaderView header;
    @InjectView(R.id.button_sign_out)
    Button signOutButton;

    private static final int PHOTO_RESULT_CODE = 123123129;

    @OnClick(R.id.user_profile_header_avatar) void onClickAvatar() {
        getActivity().findViewById(R.id.user_profile_header_avatar).setEnabled(false);
        Intent intent = new Intent(getActivity(), PhotoChooserActivity.class);
        intent.putExtra("cancelable", true);
        startActivityForResult(intent, PHOTO_RESULT_CODE);
    }


    @OnClick(R.id.button_sign_out) void onClickSignOut() {
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
                userManager.signout(new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User u, ServerError error) {
                        ((MainActivity)getActivity()).restart();
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

    @OnClick(R.id.profile_username_edittext) void onClickUsername() {
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

    @OnClick(R.id.profile_email_edittext) void onClickEmail() {
        LayoutInflater li = getActivity().getLayoutInflater();
        final View changeEmailView = li.inflate(R.layout.dialog_change_email, null);
        EditText email = (EditText) changeEmailView.findViewById(R.id.email);
        email.setText("");
        email.append(userManager.getUser().email);
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

    @OnClick(R.id.profile_password_edittext) void onClickPassword() {
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

    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myprofile, container, false);
        ButterKnife.inject(this, view);
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
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        String avatarPath = data.getExtras().getString(MediaStore.EXTRA_OUTPUT);

        Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
        header.avatarImageView.setImageBitmap(bitmap);
    }
    private void updateUser(User user) {
        getActivity().getActionBar().setTitle(user.username);
        username.setText(user.username);
        email.setText(user.email);
        header.setUser(user);
        header.avatarImageView.setImageUrl(user.avatar.big, networkingManager.getImageLoader());
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
}

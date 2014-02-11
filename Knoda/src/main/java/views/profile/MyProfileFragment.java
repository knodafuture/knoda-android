package views.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import views.core.BaseFragment;

public class MyProfileFragment extends BaseFragment {
    @InjectView(R.id.profile_username_edittext)
    TextView username;
    @InjectView(R.id.profile_email_edittext)
    TextView email;
    @InjectView(R.id.profile_view_user_header)
    UserProfileHeaderView header;


    @OnClick(R.id.profile_username_edittext) void onClickUsername() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Change Your Username");
        final EditText input = new EditText(getActivity());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final User user = new User();
                user.username = input.getText().toString();
                networkingManager.updateUser(user, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User u, ServerError error) {
                        userManager.getUser().username = user.username;
                        updateUser(userManager.getUser());
                        if (error != null)
                            return;
                    }
                });
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    @OnClick(R.id.profile_email_edittext) void onClickEmail() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Change Your Email");
        final EditText input = new EditText(getActivity());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final User user = new User();
                user.email = input.getText().toString();
                networkingManager.updateUser(user, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User u, ServerError error) {
                        userManager.getUser().email = user.email;
                        updateUser(userManager.getUser());
                        if (error != null)
                            return;
                    }
                });
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
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
    }

    private void updateUser(User user) {
        getActivity().getActionBar().setTitle(user.username);
        username.setText(user.username);
        email.setText(user.email);
        header.setUser(user);
        header.avatarImageView.setImageUrl(user.avatar.big, networkingManager.getImageLoader());
    }
}

package views.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import models.User;
import views.core.BaseFragment;

public class MyProfileFragment extends BaseFragment {

    @InjectView(R.id.profile_username_edittext)
    EditText username;
    @InjectView(R.id.profile_email_edittext)
    EditText email;
    @InjectView(R.id.profile_view_user_header)
    UserProfileHeaderView header;


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
        getActivity().getActionBar().setTitle(user.username);
        username.setText(user.username);
        email.setText(user.email);
        header.setUser(user);
        header.avatarImageView.setImageUrl(user.avatar.big, networkingManager.getImageLoader());
    }
}

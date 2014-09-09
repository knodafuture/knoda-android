package views.login;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import butterknife.InjectView;
import butterknife.OnClick;
import helpers.TypefaceSpan;
import models.User;
import views.core.BaseDialogFragment;
import views.core.MainActivity;

/**
 * Created by nick on 6/11/14.
 */
public class SignupConfirmFragment extends BaseDialogFragment {

    @InjectView(R.id.confirm_imageview)
    NetworkImageView imageView;

    @InjectView(R.id.confirm_username)
    TextView usernameTV;

    public SignupConfirmFragment() {
    }

    public static SignupConfirmFragment newInstance() {
        SignupConfirmFragment fragment = new SignupConfirmFragment();
        return fragment;
    }

    @OnClick(R.id.confirm_start_predicting_button)
    void onClick() {
        try {
            sharedPrefManager.setShouldShowVotingWalkthrough(true);

            ((MainActivity) getActivity()).onFindFriends("");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        dismissFade();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_confirm, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateBackground();

        User u = userManager.getUser();

        if (u == null)
            return;

        SpannableString s = new SpannableString(u.username.toUpperCase());
        s.setSpan(new TypefaceSpan(getActivity(), "KronaOne-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        usernameTV.setText(s);
        imageView.setImageUrl(u.avatar.small, networkingManager.getImageLoader());
    }

}

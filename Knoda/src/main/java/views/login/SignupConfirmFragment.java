package views.login;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import butterknife.InjectView;
import butterknife.OnClick;
import models.User;
import views.core.BaseDialogFragment;
import views.core.MainActivity;
import views.predictionlists.BasePredictionListFragment;

/**
 * Created by nick on 6/11/14.
 */
public class SignupConfirmFragment extends BaseDialogFragment {
    @InjectView(R.id.topview)
    RelativeLayout topview;

    @InjectView(R.id.confirm_imageview)
    NetworkImageView imageView;

    @InjectView(R.id.confirm_username)
    TextView textView;

    @OnClick(R.id.confirm_start_predicting_button) void onClick() {
        try{
            sharedPrefManager.setFirstLaunch(true);
            BasePredictionListFragment fragment = (BasePredictionListFragment) ((MainActivity) getActivity()).currentFragment;
            fragment.refreshList();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        dismissFade();
    }

    public static SignupConfirmFragment newInstance() {
        SignupConfirmFragment fragment = new SignupConfirmFragment();
        return fragment;
    }
    public SignupConfirmFragment() {}

    public void dismissFade(){
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
        topview.startAnimation(fadeOutAnimation);
        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        },300);
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
        updateBackground();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        User u = userManager.getUser();

        if (u == null)
            return;

        textView.setText(u.username + ",");

        imageView.setImageUrl(u.avatar.small, networkingManager.getImageLoader());
    }

}

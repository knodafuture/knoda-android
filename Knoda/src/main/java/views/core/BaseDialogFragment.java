package views.core;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.knoda.knoda.R;
import com.squareup.otto.Bus;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import managers.FacebookManager;
import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.TwitterManager;
import managers.UserManager;
import pubsub.LoginFlowDoneEvent;
import unsorted.ErrorReporter;
import unsorted.Logger;

/**
 * Created by nick on 6/9/14.
 */
public class BaseDialogFragment extends DialogFragment {

    @Inject
    public NetworkingManager networkingManager;

    @Inject
    public Spinner spinner;

    @Inject
    public ErrorReporter errorReporter;

    @Inject
    public UserManager userManager;

    @Inject
    public Bus bus;

    @Inject
    public SharedPrefManager sharedPrefManager;

    @Inject
    public FacebookManager facebookManager;

    @Inject
    public TwitterManager twitterManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).inject(this);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        sharedPrefManager.setShouldShowVotingWalkthrough(true);
        bus.post(new LoginFlowDoneEvent());
        Logger.log("CANCEL");
    }

    public void cancel() {
        getDialog().cancel();
    }

    public void dismissFade() {
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
        getView().startAnimation(fadeOutAnimation);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 300);
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException ex) {

        }
    }

    public void showKeyboard(View focusable) {
        focusable.requestFocus();
        InputMethodManager keyboard = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void pushFragment(Fragment fragment) {
        ((MainActivity) getActivity()).pushFragment(fragment);
    }

    public void popFragment() {
        hideKeyboard();
        ((MainActivity) getActivity()).popFragment();
    }

    public void popToRootFragment() {
        hideKeyboard();
        ((MainActivity) getActivity()).popToRootFragment();
    }

    public void setTitle(String title) {
        if (title == null)
            return;
        MainActivity activity = (MainActivity) getActivity();

        if (activity == null)
            return;
        ((MainActivity) getActivity()).setActionBarTitle(title);
    }

    public void updateBackground() {

        if (getView() == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateBackground();
                }
            }, 10);
        } else {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    File file = new File(
                            Environment.getExternalStorageDirectory()
                                    + "/blur_background.png"
                    );
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);

                    BitmapDrawable d = new BitmapDrawable(getResources(), bitmap);

                    getView().setBackgroundDrawable(d);
                }
            });
        }
    }
}

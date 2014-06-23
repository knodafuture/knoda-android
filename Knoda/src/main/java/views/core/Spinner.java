package views.core;

import android.app.DialogFragment;
import android.app.FragmentManager;

/**
 * Created by nick on 1/16/14.
 */

public class Spinner {

    private final BaseActivity mActivity;

    private static final SpinnerFragment spinnerFragment = new SpinnerFragment();

    public Spinner(BaseActivity activity) {
        this.mActivity = activity;
    }

    public void show() {
        spinnerFragment.show(mActivity.getFragmentManager().beginTransaction().addToBackStack("spinner"), "spinner");
    }

    public void hide() {
        if (spinnerFragment != null)
            spinnerFragment.dismiss();
    }

    public boolean isVisible() {
        return spinnerFragment.isVisible();
    }

}

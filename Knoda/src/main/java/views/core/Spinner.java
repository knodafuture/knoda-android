package views.core;

import android.app.ProgressDialog;
import android.widget.ProgressBar;

/**
 * Created by nick on 1/16/14.
 */

public class Spinner {

    //private static final SpinnerFragment spinnerFragment = new SpinnerFragment();
    private final BaseActivity mActivity;
    ProgressDialog progressDialog;


    public Spinner(BaseActivity activity) {
        this.mActivity = activity;
    }

    public void show() {
        //spinnerFragment.show(mActivity.getFragmentManager().beginTransaction().addToBackStack("spinner"), "spinner");
        //progressDialog = new ProgressDialog(mActivity);
        progressDialog=progressDialog.show(mActivity, null, null, true, false);
        progressDialog.setContentView(new ProgressBar(mActivity));
    }

    public void hide() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        //if (spinnerFragment != null && isVisible())
        //    spinnerFragment.dismiss();
    }

    public boolean isVisible() {
        if (progressDialog == null)
            return false;
        else
            return progressDialog.isShowing();
        //return spinnerFragment.isVisible();
    }

}

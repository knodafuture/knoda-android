package views.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

/**
 * Created by nick on 1/16/14.
 */

public class Spinner {

    //private static final SpinnerFragment spinnerFragment = new SpinnerFragment();
    private final Context context;
    ProgressDialog progressDialog;


    public Spinner(Context context) {
        this.context = context;
    }

    public void show() {
        //spinnerFragment.show(mActivity.getFragmentManager().beginTransaction().addToBackStack("spinner"), "spinner");
        //progressDialog = new ProgressDialog(mActivity);
        if (progressDialog != null && progressDialog.isShowing())
            return;
        progressDialog = progressDialog.show(context, null, null, true, false);
        progressDialog.setContentView(new ProgressBar(context));
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

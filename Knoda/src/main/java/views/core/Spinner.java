package views.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.knoda.knoda.R;

/**
 * Created by nick on 1/16/14.
 */

public class Spinner {

    private final Context context;
    ProgressDialog progressDialog;
    RotateAnimation animation;

    public Spinner(Context context) {
        this.context = context;
        animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1000);
    }

    public void show() {
        if (progressDialog != null && progressDialog.isShowing())
            return;
        progressDialog = progressDialog.show(context, null, null, true, false);
        ImageView knodaSpinner = new ImageView(context);
        knodaSpinner.setImageResource(R.drawable.ic_launcher);
        progressDialog.setContentView(knodaSpinner);
        knodaSpinner.startAnimation(animation);
    }

    public void hide() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public boolean isVisible() {
        if (progressDialog == null)
            return false;
        else
            return progressDialog.isShowing();
    }

}

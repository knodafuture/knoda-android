package views.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
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
    RotateAnimation animationSpin;

    public Spinner(Context context) {
        this.context = context;
        animationSpin = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationSpin.setInterpolator(new LinearInterpolator());
        animationSpin.setRepeatCount(1);
        animationSpin.setDuration(250);
        animationSpin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spin();
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void show() {
        if (progressDialog != null && progressDialog.isShowing())
            return;
        progressDialog = progressDialog.show(context, null, null, true, false);
        spin();

    }

    public void hide() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    private void spin() {
        if (isVisible()) {
            ImageView knodaSpinner = new ImageView(context);
            knodaSpinner.setImageResource(R.drawable.spinner);
            progressDialog.setContentView(knodaSpinner);
            knodaSpinner.startAnimation(animationSpin);
        }
    }

    public boolean isVisible() {
        if (progressDialog == null)
            return false;
        else
            return progressDialog.isShowing();
    }

}

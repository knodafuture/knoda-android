package views.core;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by nick on 1/16/14.
 */

public class Spinner {

    private final BaseActivity mActivity;

    public Spinner(BaseActivity activity) {
        this.mActivity = activity;
    }

    private View getProgressView() {
        View progressView = mActivity.progressView;

        if (!progressView.hasOnClickListeners())
            progressView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        return progressView;
    }

    public void show() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        getProgressView().setVisibility(View.VISIBLE);
        getProgressView().setAnimation(fadeIn);
    }

    public void hide() {
        getProgressView().setAlpha(0);
        getProgressView().setVisibility(View.INVISIBLE);
    }


}

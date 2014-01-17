package views.core;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by nick on 1/16/14.
 */

public class Spinner {

    private final MainActivity mActivity;

    public Spinner(MainActivity activity) {
        this.mActivity = activity;
    }

    private View getProgressView() {
        return mActivity.progressView;
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
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getProgressView().setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        getProgressView().setAnimation(fadeOut);
    }


}

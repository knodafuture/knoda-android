package listeners;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import unsorted.Logger;
import views.predictionlists.PredictionListCell;

/**
 * Created by nick on 1/30/14.
 */
public class PredictionSwipeListener implements View.OnTouchListener {


    private final static int fullRedR = 254;
    private final static int fullRedG = 50;
    private final static int fullRedB = 50;

    private final static int fullGreenR = 119;
    private final static int fullGreenG = 188;
    private final static int fullGreenB = 31;

    private final static double thresholdPercentange = 0.25;

    private double threshold;
    private int slop;
    private long animationTime;
    private ListView listView;
    private boolean paused;
    int viewWidth = 1;

    private float downX;
    private boolean swiping;
    private PredictionListCell downView;

    private PredictionCellCallbacks callbacks;


    public interface PredictionCellCallbacks {

        void onPredictionAgreed(PredictionListCell cell);
        void onPredictionDisagreed(PredictionListCell cell);
    }


    public PredictionSwipeListener(ListView listView, PredictionCellCallbacks callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        slop = vc.getScaledTouchSlop();
        animationTime = listView.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        this.listView = listView;
        this.callbacks = callbacks;
    }

    public void setEnabled(boolean enabled) {
        paused = !enabled;

        if (paused)
            reset();

    }

    public AbsListView.OnScrollListener makeScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                Logger.log(scrollState + "");
                setEnabled(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        };
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (viewWidth < 2)
            viewWidth = listView.getWidth();

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (paused)
                    break;

                Rect rect = new Rect();
                int childCount = listView.getChildCount();
                int[] coords = new int[2];
                listView.getLocationOnScreen(coords);
                int x = (int) motionEvent.getRawX() - coords[0];
                int y = (int) motionEvent.getRawY() - coords[1];

                View child;

                for (int i = 0; i < childCount; i++) {
                    child = listView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        if (child instanceof PredictionListCell)
                            downView = (PredictionListCell)child;
                        else
                            downView = null;
                        break;
                    }
                }

                if (downView != null) {
                    downX = motionEvent.getRawX();
                    threshold = (double)downView.getWidth() * thresholdPercentange;
                }

                view.onTouchEvent(motionEvent);
                return true;
            }

            case MotionEvent.ACTION_UP: {
                if (downView == null)
                    break;

                if (swiping) {

                    float deltaX = motionEvent.getRawX() - downX;

                    if (Math.abs(deltaX) > threshold) {
                        if (deltaX < 0)
                            callbacks.onPredictionDisagreed(downView);
                        else
                            callbacks.onPredictionAgreed(downView);
                    }
                } else {
                    int[] coords = new int[2];
                    downView.usernameView.getLocationOnScreen(coords);
                    int x = (int) motionEvent.getRawX() - coords[0];
                    int y = (int) motionEvent.getRawY() - coords[1];
                }


                reset();
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (downView == null)
                    break;

                //move view back to position

                reset();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (downView == null || paused)
                    break;

                if ((downView.prediction.challenge != null && downView.prediction.challenge.isOwn) || downView.prediction.expired)
                    break;

                float deltaX = motionEvent.getRawX() - downX;

                if (Math.abs(deltaX) > slop) {
                    swiping = true;
                    listView.requestDisallowInterceptTouchEvent(true);
                    // Cancel ListView's touch (un-highlighting the item)
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);

                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex()
                                    << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    listView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (swiping) {
                    if (Math.abs(deltaX) < downView.getWidth() * 0.0125)
                        deltaX = 0;

                    double percentage = Math.min(Math.abs(deltaX) / threshold, 1.0);
                    if (Math.abs(deltaX) < downView.getWidth() / 2)
                        downView.bodyView.setTranslationX(deltaX);
                    if (deltaX > 0) {
                        downView.container.setBackgroundColor(rgbColorFromPercentageOfMax(percentage, fullGreenR, fullGreenG, fullGreenB));
                        int leftMargin = ((RelativeLayout.LayoutParams)downView.agreeView.getLayoutParams()).leftMargin;

                        if (downView.bodyView.getTranslationX() > leftMargin * 2 + downView.agreeView.getWidth())
                            downView.agreeView.setTranslationX(downView.bodyView.getTranslationX() - leftMargin * 2 - downView.agreeView.getWidth());

                    } else {
                        downView.container.setBackgroundColor(rgbColorFromPercentageOfMax(percentage, fullRedR, fullRedG, fullRedB));

                        int rightMargin = ((RelativeLayout.LayoutParams)downView.disagreeView.getLayoutParams()).rightMargin;

                        if (Math.abs(downView.bodyView.getTranslationX()) > rightMargin * 2 + downView.disagreeView.getWidth()) {
                            downView.disagreeView.setX(downView.bodyView.getTranslationX() + downView.bodyView.getWidth() + rightMargin);
                        }


                    }


                    return true;
                }

                break;
            }
        }
        return false;
    }


    private int rgbColorFromPercentageOfMax(double percentange, int maxR, int maxG, int maxB) {

        double R = percentange * maxR;
        double G = percentange * maxG;
        double B = percentange * maxB;

        return Color.rgb((int)R, (int)G, (int)B);

    }

    private void reset() {
        if (downView != null) {
            downView.bodyView.animate().translationX(0).alpha(1).setDuration(animationTime).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (downView != null) {
                        downView.agreeView.setTranslationX(0);
                        downView.disagreeView.setTranslationX(0);
                        downView = null;
                    }
                    downX = 0;
                    swiping = false;
                    threshold = 0.0;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

    }


}

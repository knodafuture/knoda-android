package views.core;

/**
 * Created by nick on 1/16/14.
 */

public class Spinner {

    private static final SpinnerFragment spinnerFragment = new SpinnerFragment();
    private final BaseActivity mActivity;

    public Spinner(BaseActivity activity) {
        this.mActivity = activity;
    }

    public void show() {
        spinnerFragment.show(mActivity.getFragmentManager().beginTransaction().addToBackStack("spinner"), "spinner");
    }

    public void hide() {
        if (spinnerFragment != null && isVisible())
            spinnerFragment.dismiss();
    }

    public boolean isVisible() {
        return spinnerFragment.isVisible();
    }

}

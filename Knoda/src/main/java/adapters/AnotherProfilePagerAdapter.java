package adapters;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

import models.User;
import unsorted.Logger;
import views.core.MainActivity;
import views.predictionlists.AnotherProfilePageFragment;

public class AnotherProfilePagerAdapter extends FragmentStatePagerAdapter {
    User user;
    Context context;
    MainActivity mainActivity;

    int maxBarPixels = 0;
    int barHeight = 0;
    int onedp;

    public AnotherProfilePagerAdapter(MainActivity mainActivity, User u, Context c) {
        super(mainActivity.getFragmentManager());
        Logger.log("Activity Page created");
        this.mainActivity = mainActivity;
        user = u;
        context = c;

        onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        Display display = mainActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Point size = new Point();
        display.getSize(size);

        maxBarPixels = (size.x / 2) - (45 * onedp);
        barHeight = 35 * onedp;

    }

    @Override
    public Fragment getItem(int id) {
        return AnotherProfilePageFragment.newInstance(id, barHeight, maxBarPixels, onedp, user, mainActivity);
    }

    @Override
    public int getCount() {
        return 2;
    }


}

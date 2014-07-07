package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.knoda.knoda.R;

import unsorted.Logger;
import views.activity.ActivityBoardFragment;
import views.activity.ActivityTypeFragment;

public class ActivityPagerAdapter extends FragmentPagerAdapter {

    public ActivityPagerAdapter(FragmentManager fm) {
        super(fm);
        Logger.log("Activity Page created");
    }

    @Override
    public Fragment getItem(int id) {
        Logger.log("Activity Page filter " + id);
        Fragment fragment = ActivityTypeFragment.newInstance(id);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}

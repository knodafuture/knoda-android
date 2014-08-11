package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

import unsorted.Logger;
import views.activity.ActivityTypeFragment;

public class ActivityPagerAdapter extends FragmentStatePagerAdapter {

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

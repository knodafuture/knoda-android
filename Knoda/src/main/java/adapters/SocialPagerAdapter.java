package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import unsorted.Logger;
import views.activity.ActivityTypeFragment;
import views.group.GroupFragment;

public class SocialPagerAdapter extends FragmentPagerAdapter {

    public SocialPagerAdapter(FragmentManager fm) {
        super(fm);
        Logger.log("Social Page created");
    }

    @Override
    public Fragment getItem(int id) {
        Logger.log("Social Page filter " + id);
        if (id == 0)
            return ActivityTypeFragment.newInstance(id);
        else if (id == 1)
            return GroupFragment.newInstance();

        return new Fragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}

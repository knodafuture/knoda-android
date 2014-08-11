package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import unsorted.Logger;
import views.contests.ContestFragment;
import views.group.GroupFragment;

public class SocialPagerAdapter extends FragmentStatePagerAdapter {

    public SocialPagerAdapter(FragmentManager fm) {
        super(fm);
        Logger.log("Social Page created");
    }

    @Override
    public Fragment getItem(int id) {
        Logger.log("Social Page filter " + id);
        if (id == 0)
            return ContestFragment.newInstance("entered");
        else if (id == 1)
            return GroupFragment.newInstance();

        return new Fragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}

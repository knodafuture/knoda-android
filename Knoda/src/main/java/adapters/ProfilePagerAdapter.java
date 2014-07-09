package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import unsorted.Logger;
import views.profile.MyProfileFeedFragment;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    public ProfilePagerAdapter(FragmentManager fm) {
        super(fm);
        Logger.log("Profile Page created");
    }

    @Override
    public Fragment getItem(int id) {
        Logger.log("Profile Page filter " + id);
        Fragment fragment = MyProfileFeedFragment.newInstance(id);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

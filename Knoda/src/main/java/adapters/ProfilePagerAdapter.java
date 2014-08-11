package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

import unsorted.Logger;
import views.profile.MyProfileFeedFragment;
import views.profile.MyProfileFragment;

public class ProfilePagerAdapter extends FragmentStatePagerAdapter {
    MyProfileFragment parentFragment;

    public ProfilePagerAdapter(FragmentManager fm, MyProfileFragment fragment) {
        super(fm);
        parentFragment = fragment;
        Logger.log("Profile Page created");
    }

    @Override
    public Fragment getItem(int id) {
        Logger.log("Profile Page filter " + id);
        Fragment fragment = MyProfileFeedFragment.newInstance(id, parentFragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

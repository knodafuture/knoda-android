package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import unsorted.Logger;
import views.activity.ActivityBoardFragment;

public class ActivityPagerAdapter extends FragmentPagerAdapter {

    public ActivityPagerAdapter(FragmentManager fm) {
        super(fm);
        Logger.log("Activity Page created");
    }

    @Override
    public Fragment getItem(int position) {
        Logger.log("Activity Page filter " + position);
        String board = "invites";
        switch (position) {
            case 0:
                board = "all";
                break;
            case 1:
                board = "expired";
                break;
            case 2:
                board = "comments";
                break;
        }
        Fragment fragment = ActivityBoardFragment.newInstance(board);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}

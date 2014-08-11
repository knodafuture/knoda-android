package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

import models.Group;
import unsorted.Logger;
import views.group.GroupLeaderboardFragment;

public class LeaderboardPagerAdapter extends FragmentStatePagerAdapter {
    public Group group;

    public LeaderboardPagerAdapter(FragmentManager fm, Group group) {
        super(fm);
        this.group = group;
        Logger.log("LEADERBOARD# created adapter for group " + group.id);
    }

    @Override
    public Fragment getItem(int position) {
        Logger.log("LEADERBOARD# for position" + position + " group " + group.id);
        String board = "weekly";
        switch (position) {
            case 1:
                board = "monthly";
                break;
            case 2:
                board = "alltime";
                break;
        }
        Fragment fragment = GroupLeaderboardFragment.newInstance(group, board);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}

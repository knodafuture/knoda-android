package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import unsorted.Logger;
import views.contests.ContestDetailFeedFragment;

public class ContestDetailPagerAdapter extends FragmentPagerAdapter {
    int contestId;

    public ContestDetailPagerAdapter(FragmentManager fm, int contestId) {
        super(fm);
        this.contestId = contestId;
        Logger.log("Contest Detail created");
    }

    @Override
    public Fragment getItem(int id) {
        if (id == 0) {
            ContestDetailFeedFragment fragment = ContestDetailFeedFragment.newInstance(null, contestId);
            return fragment;
        } else if (id == 1) {
            ContestDetailFeedFragment fragment = ContestDetailFeedFragment.newInstance("expired", contestId);
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

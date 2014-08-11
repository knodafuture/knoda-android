package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

import unsorted.Logger;
import views.contests.ContestDetailFeedFragment;
import views.contests.ContestDetailFragment;

public class ContestDetailPagerAdapter extends FragmentStatePagerAdapter {
    int contestId;
    ContestDetailFragment parentFragment;

    public ContestDetailPagerAdapter(FragmentManager fm, int contestId, ContestDetailFragment fragment) {
        super(fm);
        this.contestId = contestId;
        this.parentFragment = fragment;
        Logger.log("Contest Detail created");
    }

    @Override
    public Fragment getItem(int id) {
        if (id == 0) {
            ContestDetailFeedFragment fragment = ContestDetailFeedFragment.newInstance(null, contestId, parentFragment);
            return fragment;
        } else if (id == 1) {
            ContestDetailFeedFragment fragment = ContestDetailFeedFragment.newInstance("expired", contestId, parentFragment);
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

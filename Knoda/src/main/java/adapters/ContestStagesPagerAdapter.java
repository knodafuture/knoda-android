package adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import models.Contest;
import unsorted.Logger;
import views.contests.ContestLeaderboardFeedFragment;

public class ContestStagesPagerAdapter extends FragmentPagerAdapter {
    Contest contest;

    public ContestStagesPagerAdapter(FragmentManager fm, Contest contest) {
        super(fm);
        this.contest = contest;
        Logger.log("Contest Leaderboard created");
    }

    @Override
    public Fragment getItem(int id) {
        return ContestLeaderboardFeedFragment.newInstance(contest.contestStages.get(id));
    }

    @Override
    public int getCount() {
        return contest.contestStages.size();
    }
}

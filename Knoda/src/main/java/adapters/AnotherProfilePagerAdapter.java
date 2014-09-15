package adapters;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.User;
import unsorted.Logger;
import views.core.MainActivity;

public class AnotherProfilePagerAdapter extends FragmentStatePagerAdapter {
    User user;
    Context context;
    MainActivity mainActivity;

    public AnotherProfilePagerAdapter(MainActivity mainActivity, User u, Context c) {
        super(mainActivity.getFragmentManager());
        Logger.log("Activity Page created");
        this.mainActivity = mainActivity;
        user = u;
        context = c;

    }

    @Override
    public Fragment getItem(int id) {
        return new AnotherProfilePageFragment(id);
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void setStreak(String streak, TextView streakTV) {
        if (streak == null || streak == "") {
            streakTV.setText("W0");
        } else {
            streakTV.setText(streak);
        }
    }

    public class AnotherProfilePageFragment extends Fragment {
        int x;

        public AnotherProfilePageFragment(int x) {
            this.x = x;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View v = new View(getActivity());

            if (x == 0) {
                v = inflater.inflate(R.layout.view_profile_stats, container, false);
                TextView pointsTextView = (TextView) v.findViewById(R.id.profile_points);
                TextView winPercentTextView = (TextView) v.findViewById(R.id.profile_winpercent);
                TextView streakTextView = (TextView) v.findViewById(R.id.profile_winstreak);
                TextView winLossTextView = (TextView) v.findViewById(R.id.profile_winloss);

                pointsTextView.setText(user.points.toString());
                winLossTextView.setText(user.won.toString() + "-" + user.lost.toString());
                setStreak(user.streak, streakTextView);
                winPercentTextView.setText(user.winningPercentage.toString() + "%");

            } else if (x == 1) {
                v = inflater.inflate(R.layout.view_profile_head_to_head, container, false);
                NetworkImageView avatar1 = (NetworkImageView) v.findViewById(R.id.profile_avatar1);
                NetworkImageView avatar2 = (NetworkImageView) v.findViewById(R.id.profile_avatar2);

                avatar1.setImageUrl(mainActivity.userManager.getUser().avatar.small, mainActivity.networkingManager.getImageLoader());
                avatar2.setImageUrl(user.avatar.small, mainActivity.networkingManager.getImageLoader());

                if (user.rivalry != null) {
                    ((TextView) v.findViewById(R.id.head_to_head_win1)).setText(user.rivalry.opponent_won+"");
                    ((TextView) v.findViewById(R.id.head_to_head_win2)).setText(user.rivalry.user_won+"");
                }

            }
            return v;
        }


    }

}

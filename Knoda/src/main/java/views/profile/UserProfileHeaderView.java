package views.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.User;

/**
 * Created by nick on 2/3/14.
 */
public class UserProfileHeaderView extends RelativeLayout {

    public TextView pointsTextView;
    public TextView winPercentTextView;
    public TextView streakTextView;
    public TextView winLossTextView;
    public TextView tv_followers;
    public TextView tv_following;

    public NetworkImageView avatarImageView;

    public UserProfileHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public UserProfileHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_another_user_header, this);

        pointsTextView = (TextView) findViewById(R.id.profile_points);
        winPercentTextView = (TextView) findViewById(R.id.profile_winpercent);
        streakTextView = (TextView) findViewById(R.id.profile_winstreak);
        winLossTextView = (TextView) findViewById(R.id.profile_winloss);
        tv_followers = (TextView) findViewById(R.id.profile_followers);
        tv_following = (TextView) findViewById(R.id.profile_following);

        avatarImageView = (NetworkImageView) findViewById(R.id.profile_avatar);

    }


    public void setUser(User user) {
        pointsTextView.setText(user.points.toString());
        winLossTextView.setText(user.won.toString() + "-" + user.lost.toString());
        setStreak(user.streak);
        winPercentTextView.setText(user.winningPercentage.toString() + "%");
        tv_following.setText(user.following_count + "");
        tv_followers.setText(user.follower_count + "");
    }

    public void setStreak(String streak) {
        if (streak == null || streak == "") {
            streakTextView.setText("W0");
        } else {
            streakTextView.setText(streak);
        }
    }
}

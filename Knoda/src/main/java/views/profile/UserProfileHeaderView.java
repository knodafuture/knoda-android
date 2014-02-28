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
        LayoutInflater.from(context).inflate(R.layout.view_user_header, this);

        pointsTextView = (TextView)findViewById(R.id.user_profile_header_points_textview);
        winPercentTextView = (TextView)findViewById(R.id.user_profile_header_win_textview);
        streakTextView = (TextView)findViewById(R.id.user_profile_header_streak_textview);
        winLossTextView = (TextView)findViewById(R.id.user_profile_header_wl_textview);

        avatarImageView = (NetworkImageView)findViewById(R.id.user_profile_header_avatar);

    }


    public void setUser(User user) {
        pointsTextView.setText(user.points.toString());
        winLossTextView.setText(user.won.toString() + "-" + user.lost.toString());
        setStreak(user.streak);
        winPercentTextView.setText(user.winningPercentage.toString() + "%");
    }

    public void setStreak(String streak) {
        if (streak == null || streak == "") {
            streakTextView.setText("W0");
        }else {
            streakTextView.setText(streak.toString());
        }
    }
}

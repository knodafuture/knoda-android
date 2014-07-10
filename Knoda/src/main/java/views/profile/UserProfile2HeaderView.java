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
public class UserProfile2HeaderView extends RelativeLayout {

    public TextView pointsTextView;
    public TextView winPercentTextView;
    public TextView streakTextView;
    public TextView winLossTextView;

    public NetworkImageView avatarImageView;

    public UserProfile2HeaderView(Context context) {
        super(context);
        initView(context);
    }

    public UserProfile2HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.fragment_anotherprofile, this);

        pointsTextView = (TextView) findViewById(R.id.profile_points);
        winPercentTextView = (TextView) findViewById(R.id.profile_winpercent);
        streakTextView = (TextView) findViewById(R.id.profile_winstreak);
        winLossTextView = (TextView) findViewById(R.id.profile_winloss);

        avatarImageView = (NetworkImageView) findViewById(R.id.profile_avatar);

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
        } else {
            streakTextView.setText(streak);
        }
    }
}

package views.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

/**
 * Created by nick on 2/3/14.
 */
public class UserProfileHeaderView extends RelativeLayout {

    public TextView pointsTextView;
    public TextView winPercentTextView;
    public TextView streakTextView;
    public TextView winLossTextView;

    public NetworkImageView avatarImageView;


    public UserProfileHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_user_header, this);
    }

    @Override
    public void onFinishInflate() {
        Log.e("USERPROFILEHEADERVIEW", "INFLATED");
        pointsTextView = (TextView)findViewById(R.id.user_profile_header_points_textview);
        winPercentTextView = (TextView)findViewById(R.id.user_profile_header_win_textview);
        streakTextView = (TextView)findViewById(R.id.user_profile_header_streak_textview);
        winLossTextView = (TextView)findViewById(R.id.user_profile_header_wl_textview);

        avatarImageView = (NetworkImageView)findViewById(R.id.user_profile_header_avatar);
    }
}

package views.contests;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.ContestUser;

/**
 * Created by jeff on 7/31/2014.
 */
public class ContestUserListCell extends RelativeLayout {

    final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
    AbsListView.LayoutParams lp_header = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, onedp * 20);
    AbsListView.LayoutParams lp_normal = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, onedp * 40);
    public TextView rankTV;
    public TextView usernameTV;
    public TextView winsTV;
    public NetworkImageView avatarImageView;
    public ImageView mask;

    public ContestUserListCell(Context context) {
        super(context);
    }

    public ContestUserListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        rankTV = (TextView) findViewById(R.id.contestuser_rank);
        usernameTV = (TextView) findViewById(R.id.contestuser_username);
        avatarImageView = (NetworkImageView) findViewById(R.id.contestuser_avatar);
        winsTV = (TextView) findViewById(R.id.contestuser_wins);
        mask = (ImageView) findViewById(R.id.contestuser_avatar_mask);
        rankTV.setGravity(Gravity.CENTER);
        usernameTV.setGravity(Gravity.CENTER_VERTICAL);
        winsTV.setGravity(Gravity.CENTER_VERTICAL);
    }

    public void setHeaderMode() {
        this.setBackgroundColor(Color.parseColor("#efefef"));
        rankTV.setText("RANK");
        rankTV.setTextColor(Color.BLACK);
        rankTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        usernameTV.setText("USERNAME");
        usernameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        winsTV.setText("WINS");
        winsTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        avatarImageView.setBackgroundDrawable(null);
        avatarImageView.setImageDrawable(null);
        mask.setVisibility(INVISIBLE);
        this.setLayoutParams(lp_header);

    }

    public void setContestUser(ContestUser contestUser) {
        this.setLayoutParams(lp_normal);
        if (contestUser.rank % 2 != 0) {
            this.setBackgroundColor(Color.parseColor("#ffffff"));
            mask.setImageResource(R.drawable.leaderboard_mask);
        } else {
            this.setBackgroundColor(Color.parseColor("#efefef"));
            mask.setImageResource(R.drawable.leaderboard_gray_mask);
        }
        rankTV.setText(contestUser.rank + "");
        rankTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        rankTV.setTextColor(getResources().getColor(R.color.knodaLightGreen));
        usernameTV.setText(contestUser.username);
        usernameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        winsTV.setText(contestUser.won + "");
        winsTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        avatarImageView.setBackgroundColor(Color.BLACK);
        mask.setVisibility(VISIBLE);
    }
}

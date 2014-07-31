package views.contests;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.ContestUser;

/**
 * Created by jeff on 7/31/2014.
 */
public class ContestUserListCell extends RelativeLayout {

    public TextView rankTV;
    public TextView usernameTV;
    public TextView winsTV;

    public NetworkImageView avatarImageView;

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
        rankTV.setGravity(Gravity.CENTER);
        usernameTV.setGravity(Gravity.CENTER_VERTICAL);
        winsTV.setGravity(Gravity.CENTER_VERTICAL);
    }

    public void setHeaderMode() {
        this.setBackgroundColor(Color.parseColor("#cccccc"));
        rankTV.setText("RANK");
        rankTV.setTextColor(Color.BLACK);
        rankTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        usernameTV.setText("USERNAME");
        usernameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        winsTV.setText("WINS");
        winsTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        avatarImageView.setBackgroundDrawable(null);
        avatarImageView.setImageDrawable(null);

    }

    public void setContestUser(ContestUser contestUser) {
        if (contestUser.rank % 2 != 0)
            this.setBackgroundColor(Color.parseColor("#ffffff"));
        else
            this.setBackgroundColor(Color.parseColor("#cccccc"));
        rankTV.setText(contestUser.rank + "");
        rankTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        rankTV.setTextColor(getResources().getColor(R.color.knodaLightGreen));
        usernameTV.setText(contestUser.username);
        usernameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        winsTV.setText(contestUser.won + "");
        winsTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        avatarImageView.setBackgroundColor(Color.BLACK);
    }
}

package views.contests;

import android.content.Context;
import android.util.AttributeSet;
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
    }

    public void setHeaderMode() {

    }

    public void setContestUser(ContestUser contestUser) {
        rankTV.setText(contestUser.rank + "");
        usernameTV.setText(contestUser.username);
        winsTV.setText(contestUser.won + "");
    }
}

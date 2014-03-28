package views.group;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import models.Leader;

public class LeaderListCell  extends RelativeLayout {
    public TextView nameView;
    public TextView winsView;
    public TextView winpercentView;

    public Leader leader;

    public LeaderListCell(Context context) {
        super(context);
    }

    public LeaderListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onFinishInflate() {
        nameView = (TextView) findViewById(R.id.group_leader_username_textview);
        winsView = (TextView) findViewById(R.id.group_leader_wins_textview);
        winpercentView = (TextView) findViewById(R.id.group_leader_winpercent_textview);
    }


    public void setLeader(Leader leader) {
        this.leader = leader;
        update();
    }

    public void update() {
        nameView.setText(leader.username);
        winsView.setText(leader.won.toString() + "-" + leader.lost.toString());
        winpercentView.setText(leader.getWinPercentageString());

    }
}

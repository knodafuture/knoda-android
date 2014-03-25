package views.group;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.Group;

public class GroupListCell extends RelativeLayout {
    public NetworkImageView avatarImageView;
    public TextView nameView;
    public TextView leaderView;
    public TextView myRankView;
    public TextView memberCountView;

    public Group group;

    public GroupListCell(Context context) {
        super(context);
    }

    public GroupListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onFinishInflate() {
        nameView = (TextView)findViewById(R.id.group_cell_name);
        leaderView = (TextView) findViewById(R.id.group_leader_username);
        myRankView = (TextView) findViewById(R.id.group_my_rank);
        avatarImageView = (NetworkImageView) findViewById(R.id.group_cell_avatar_imageview);
        memberCountView = (TextView) findViewById(R.id.group_member_count);
    }


    public void setGroup(Group group) {
        this.group = group;
        update();

    }

    public void update() {
        nameView.setText(group.name);
        leaderView.setText(group.leader.username);
        myRankView.setText(group.rank.rank.toString());
        memberCountView.setText("rank (" + group.memberCount.toString() + ")");
    }
}

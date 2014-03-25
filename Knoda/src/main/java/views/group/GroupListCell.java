package views.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.Group;
import unsorted.Logger;

public class GroupListCell extends RelativeLayout {
    public NetworkImageView avatarImageView;
    public TextView nameView;

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
        avatarImageView = (NetworkImageView) findViewById(R.id.group_cell_avatar_imageview);
    }


    public void setGroup(Group group) {
        this.group = group;
        update();

    }

    public void update() {
        nameView.setText(group.name);
    }
}

package views.activity;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import models.ActivityItem;
import models.Group;

public class GroupListCell extends RelativeLayout {
    public TextView nameView;

    public Group group;

    public GroupListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onFinishInflate() {
        nameView = (TextView)findViewById(R.id.group_cell_name);
    }


    public void setGroup(Group group) {
        this.group = group;
        update();

    }

    public void update() {
        nameView.setText(group.name);
    }
}

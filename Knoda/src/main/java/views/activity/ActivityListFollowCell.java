package views.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.ActivityItem;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityListFollowCell extends RelativeLayout {


    public NetworkImageView iconImageView;
    public TextView username;
    public TextView title;
    public Button followbutton;
    public View cover;
    public View divider;

    public ActivityListFollowCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onFinishInflate() {
        iconImageView = (NetworkImageView) findViewById(R.id.winloss_imageview);
        username = (TextView) findViewById(R.id.winloss_comment);
        title=(TextView)findViewById(R.id.winloss_title);
        followbutton = (Button) findViewById(R.id.follow_user_button);
        cover = findViewById(R.id.follow_user_button_cover);
        divider = findViewById(R.id.divider);
    }
}

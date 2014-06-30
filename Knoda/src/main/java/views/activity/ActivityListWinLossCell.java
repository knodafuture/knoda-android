package views.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.ActivityItem;
import models.ActivityItemType;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityListWinLossCell extends RelativeLayout {


    public NetworkImageView iconImageView;
    public TextView winlosstitle;
    public TextView winlosscomment;
    public TextView winlossbutton;
    public RelativeLayout buttonContainer;
    public View divider;

    final static String bragbg = "#EBF5DE";
    final static String bragborder = "#77BC1F";

    final static String settlebg = "#FFE1E1";
    final static String settleborder = "#FE3232";

    final static String groupbg = "#DEE7E1";
    final static String groupborder = "#235C37";

    public ActivityItem activityItem;

    public ActivityListWinLossCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onFinishInflate() {
        iconImageView = (NetworkImageView) findViewById(R.id.winloss_imageview);
        winlosstitle = (TextView) findViewById(R.id.winloss_title);
        winlosscomment = (TextView) findViewById(R.id.winloss_comment);
        winlossbutton = (TextView) findViewById(R.id.winloss_button);
        buttonContainer = (RelativeLayout) findViewById(R.id.winloss_button_container);
        divider = findViewById(R.id.divider);
    }

    public void setActivityItem(ActivityItem activityItem, ImageLoader imageLoader) {
        this.activityItem = activityItem;
        update(imageLoader);
    }

    public void update(ImageLoader imageLoader) {

        if (activityItem.type.equals(ActivityItemType.COMMENT)) {
            hideButton();
        } else if (activityItem.type == ActivityItemType.WON) {
            winlossbutton.setText("Brag");
            //activityItem.title = "<font color='#77BC1F'>You Won</font>" + activityItem.title;
            winlossbutton.setTextColor(Color.parseColor(bragborder));
            winlossbutton.setBackgroundResource(R.drawable.brag_button);
        } else if (activityItem.type == ActivityItemType.LOST) {
            //activityItem.title = "<font color='#FE3232'>You Lost</font>" + activityItem.title;
            hideButton();
        } else if (activityItem.type == ActivityItemType.INVITATION) {
            iconImageView.setBackgroundResource(R.drawable.ic_notification_group);
            winlossbutton.setText("Join");
            winlossbutton.setTextColor(Color.parseColor(groupborder));
            winlossbutton.setBackgroundResource(R.drawable.group_button);
        } else if (activityItem.type == ActivityItemType.EXPIRED) {
            iconImageView.setBackgroundResource(R.drawable.ic_notification_settle);
            winlossbutton.setText("Let's Settle it!");
            winlossbutton.setTextColor(Color.parseColor(settleborder));
            winlossbutton.setBackgroundResource(R.drawable.settle_button);
        }

        if (activityItem.image_url != null)
            iconImageView.setImageUrl(activityItem.image_url, imageLoader);
        if (activityItem.title != null)
            winlosstitle.setText(Html.fromHtml(activityItem.title));
        if (activityItem.body != null)
            winlosscomment.setText("\""+ activityItem.body+"\"");
    }

    public void hideButton() {
        winlossbutton.setVisibility(INVISIBLE);
        ViewGroup.LayoutParams lp = winlossbutton.getLayoutParams();
        lp.height = 0;
        winlossbutton.setLayoutParams(lp);
        buttonContainer.setLayoutParams(lp);
    }
}

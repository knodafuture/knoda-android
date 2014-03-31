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

/**
 * Created by nick on 2/1/14.
 */
public class ActivityListCell extends RelativeLayout {


    public ImageView iconImageView;
    public TextView textView;
    public TextView metadataTextView;

    public ActivityItem activityItem;

    public ActivityListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onFinishInflate() {
        iconImageView = (ImageView)findViewById(R.id.activity_cell_imageview);
        textView = (TextView)findViewById(R.id.activity_cell_textview);
        metadataTextView = (TextView)findViewById(R.id.activity_cell_metadata_textview);
    }


    public void setActivityItem(ActivityItem activityItem) {
        this.activityItem = activityItem;
        update();

    }

    public void update() {
        int resId = 0;

        switch (activityItem.type) {
            case WON:
                resId = R.drawable.activity_won_icon;
                break;
            case LOST:
                resId = R.drawable.activity_lost_icon;
                break;
            case EXPIRED:
                resId = R.drawable.activity_expired_icon;
                break;
            case COMMENT:
                resId = R.drawable.activity_comment_icon;
                break;
            case INVITATION:
                resId = R.drawable.activity_groups_icon;

        }

        if (resId != 0)
            iconImageView.setImageResource(resId);

        metadataTextView.setText(activityItem.getCreationString());
        String t = activityItem.text.replace("<p>", "");
        t = t.replace("</p>", "");
        String[] parts = t.split("</b>");
        String firstString = parts[0].replace("<b>", "");
        String secondString = parts[1];
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(firstString + secondString);
        stringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, firstString.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        stringBuilder.setSpan(new ForegroundColorSpan(Color.rgb(102, 102, 102)), firstString.length(),
                firstString.length() + secondString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(stringBuilder.toString());
        textView.setLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
    }
}

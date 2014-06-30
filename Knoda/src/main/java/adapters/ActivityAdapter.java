package adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import helpers.AdapterHelper;
import models.ActivityItem;
import models.ActivityItemType;
import views.activity.ActivityListWinLossCell;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityAdapter extends PagingAdapter<ActivityItem> {

    public ActivityAdapter(Context context, PagingAdapterDatasource<ActivityItem> datasource, ImageLoader imageLoader) {
        super(context, datasource, imageLoader);
    }

    static final String bragborder = "#77BC1F";
    static final String settleborder = "#FE3232";
    static final String groupborder = "#235C37";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);

        ActivityListWinLossCell listItem = (ActivityListWinLossCell) AdapterHelper.getConvertViewSafely(convertView, ActivityListWinLossCell.class);
        if (listItem == null)
            listItem = (ActivityListWinLossCell) LayoutInflater.from(context).inflate(R.layout.list_cell_activity_winloss, null);

        ActivityItem item = getItem(position);

        listItem.setTag(item);

        if (position == objects.size() - 1)
            listItem.divider.setVisibility(View.INVISIBLE);

        //listItem.setActivityItem(item, imageLoader);
        update(listItem, item);

        return listItem;
    }

    private void update(View v, ActivityItem activityItem) {

        NetworkImageView iconImageView = (NetworkImageView) v.findViewById(R.id.winloss_imageview);
        TextView winlosstitle = (TextView) v.findViewById(R.id.winloss_title);
        TextView winlosscomment = (TextView) v.findViewById(R.id.winloss_comment);
        TextView winlossbutton = (TextView) v.findViewById(R.id.winloss_button);
        RelativeLayout buttonContainer = (RelativeLayout) v.findViewById(R.id.winloss_button_container);

        if (activityItem.type.equals(ActivityItemType.COMMENT)) {
            iconImageView.setBackgroundResource(R.drawable.ic_notification_avatar);
            setUpButton(winlossbutton, buttonContainer, "", false);

        } else if (activityItem.type == ActivityItemType.WON) {
            iconImageView.setBackgroundResource(R.drawable.ic_notification_avatar);
            setUpButton(winlossbutton, buttonContainer, "Brag", true);
            //activityItem.title = "<font color='#77BC1F'>You Won</font>" + activityItem.title;
            winlossbutton.setTextColor(Color.parseColor(bragborder));
            winlossbutton.setBackgroundResource(R.drawable.brag_button);
            winlossbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
                    b.setTitle("test")
                            .setMessage("test")
                            .show();
                }
            });

        } else if (activityItem.type == ActivityItemType.LOST) {
            iconImageView.setBackgroundResource(R.drawable.ic_notification_avatar);
            //activityItem.title = "<font color='#FE3232'>You Lost</font>" + activityItem.title;
            setUpButton(winlossbutton, buttonContainer, "", false);

        } else if (activityItem.type == ActivityItemType.INVITATION) {
            iconImageView.setBackgroundResource(R.drawable.ic_notification_group);
            setUpButton(winlossbutton, buttonContainer, activityItem.body, true);
            winlossbutton.setTextColor(Color.parseColor(groupborder));
            winlossbutton.setBackgroundResource(R.drawable.group_button);
            hideBody(winlosscomment);

        } else if (activityItem.type == ActivityItemType.EXPIRED) {
            iconImageView.setBackgroundResource(R.drawable.ic_notification_settle);
            setUpButton(winlossbutton, buttonContainer, "Let's Settle it!", true);
            winlossbutton.setTextColor(Color.parseColor(settleborder));
            winlossbutton.setBackgroundResource(R.drawable.settle_button);
        }

        if (activityItem.image_url != null)
            iconImageView.setImageUrl(activityItem.image_url, imageLoader);

        if (activityItem.title != null)
            winlosstitle.setText(Html.fromHtml(activityItem.title));
        if (activityItem.body != null && activityItem.type != ActivityItemType.INVITATION)
            winlosscomment.setText("\"" + activityItem.body + "\"");


    }

    public void setUpButton(TextView winlossbutton, RelativeLayout buttonContainer, String buttontext, boolean show) {
        if (!show) {
            winlossbutton.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams lp = buttonContainer.getLayoutParams();
            lp.height = 0;
            buttonContainer.setLayoutParams(lp);
        } else {
            winlossbutton.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp2 = buttonContainer.getLayoutParams();
            lp2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            buttonContainer.setLayoutParams(lp2);
            if (buttontext != null)
                winlossbutton.setText(Html.fromHtml(buttontext));
        }

    }

    public void hideBody(TextView bodytext) {
        bodytext.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams lp = bodytext.getLayoutParams();
        lp.height = 0;
        bodytext.setLayoutParams(lp);
    }


}

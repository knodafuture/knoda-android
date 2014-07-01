package adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import java.util.ArrayList;

import helpers.AdapterHelper;
import models.ActivityItem;
import models.ActivityItemType;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import views.activity.ActivityListWinLossCell;
import views.core.MainActivity;
import views.group.CreateGroupHeaderView;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityAdapter extends PagingAdapter<ActivityItem> {

    public ActivityAdapter(Context context, PagingAdapterDatasource<ActivityItem> datasource, ImageLoader imageLoader, Activity activity, String filter) {
        super(context, datasource, imageLoader);
        this.activity = activity;
        this.filter = filter;
        userPic = (BitmapDrawable) activity.getResources().getDrawable(R.drawable.ic_notification_avatar);
        imageLoader.get(((MainActivity) activity).userManager.getUser().avatar.thumb, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                userPic = new BitmapDrawable(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    BitmapDrawable userPic;
    String filter = "all";
    final Activity activity;
    static final String bragborder = "#77BC1F";
    static final String settleborder = "#FE3232";
    static final String groupborder = "#235C37";


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);
        ActivityItem item = getItem(position);

        if (!matchFilter(item.type)) {
            return LayoutInflater.from(context).inflate(R.layout.list_cell_empty, null);
        } else {
            ActivityListWinLossCell listItem = (ActivityListWinLossCell) AdapterHelper.getConvertViewSafely(convertView, ActivityListWinLossCell.class);
            if (listItem == null)
                listItem = (ActivityListWinLossCell) LayoutInflater.from(context).inflate(R.layout.list_cell_activity_winloss, null);


            listItem.setTag(item);

            if (position == objects.size() - 1)
                listItem.divider.setVisibility(View.INVISIBLE);

            //listItem.setActivityItem(item, imageLoader);
            update(listItem, item);

            return listItem;
        }
    }

    private boolean matchFilter(ActivityItemType type) {
        if (filter.equals("all"))
            return true;
        else if (filter.equals("expired") && type == ActivityItemType.EXPIRED)
            return true;
        else if (filter.equals("comments") && type == ActivityItemType.COMMENT)
            return true;
        else if (filter.equals("invites") && type == ActivityItemType.INVITATION)
            return true;
        else
            return false;
    }

    private void update(View v, ActivityItem activityItem) {

        NetworkImageView iconImageView = (NetworkImageView) v.findViewById(R.id.winloss_imageview);
        TextView winlosstitle = (TextView) v.findViewById(R.id.winloss_title);
        TextView winlosscomment = (TextView) v.findViewById(R.id.winloss_comment);
        TextView winlossbutton = (TextView) v.findViewById(R.id.winloss_button);
        RelativeLayout buttonContainer = (RelativeLayout) v.findViewById(R.id.winloss_button_container);

        if (activityItem.type == ActivityItemType.COMMENT && (filter.equals("all") || filter.equals("comments"))) {
            setImage(iconImageView, R.drawable.ic_notification_avatar);
            setUpButton(winlossbutton, buttonContainer, "", false);
            setUpBody(winlosscomment, true);

        } else if (activityItem.type == ActivityItemType.WON && filter.equals("all")) {
            setImage(iconImageView, R.drawable.ic_notification_avatar);
            setUpButton(winlossbutton, buttonContainer, "Brag", true);
            setUpBody(winlosscomment, true);
            //activityItem.title = "<font color='#77BC1F'>You Won</font>" + activityItem.title;
            winlossbutton.setTextColor(Color.parseColor(bragborder));
            winlossbutton.setBackgroundResource(R.drawable.brag_button);
            winlossbutton.setTag(activityItem.target);
            winlossbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) activity).spinner.show();
                    ((MainActivity) activity).networkingManager.getPrediction(Integer.parseInt((String) v.getTag()), new NetworkCallback<Prediction>() {
                        @Override
                        public void completionHandler(Prediction prediction, ServerError error) {
                            if (activity == null)
                                return;
                            ((MainActivity) activity).spinner.hide();
                            if (error == null) {
                                String prefix = "";
                                if (prediction.userId == ((MainActivity) activity).userManager.getUser().id) {
                                    prefix = "I won my prediction: ";
                                } else if (prediction.outcome)
                                    prefix = "I agreed and won: ";
                                else
                                    prefix = "I disagreed and won: ";

                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("text/plain");
                                String suffix = " via @KNODAfuture " + prediction.shortUrl;
                                int predictionLength = 139 - suffix.length();
                                String text = "";
                                if (prediction.body.length() > predictionLength) {
                                    text = prefix + prediction.body.substring(0, predictionLength - 3) + "..." + suffix;
                                } else {
                                    text = prefix + prediction.body + suffix;
                                }
                                share.putExtra(Intent.EXTRA_TEXT, text);
                                activity.startActivity(Intent.createChooser(share, "How would you like to share?"));
                            } else
                                Toast.makeText(activity.getApplicationContext(), "There was an error trying to share", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            });

        } else if (activityItem.type == ActivityItemType.LOST && filter.equals("all")) {
            //activityItem.title = "<font color='#FE3232'>You Lost</font>" + activityItem.title;
            setUpButton(winlossbutton, buttonContainer, "", false);
            setUpBody(winlosscomment, true);

        } else if (activityItem.type == ActivityItemType.INVITATION && (filter.equals("all") || filter.equals("invites"))) {
            setImage(iconImageView, R.drawable.ic_notification_group);
            setUpButton(winlossbutton, buttonContainer, activityItem.body, true);
            winlossbutton.setTextColor(Color.parseColor(groupborder));
            winlossbutton.setBackgroundResource(R.drawable.group_button);
            setUpBody(winlosscomment, false);

        } else if (activityItem.type == ActivityItemType.EXPIRED && (filter.equals("all") || filter.equals("expired"))) {
            setImage(iconImageView, R.drawable.ic_notification_settle);
            setUpButton(winlossbutton, buttonContainer, "Let's Settle it!", true);
            winlossbutton.setTextColor(Color.parseColor(settleborder));
            winlossbutton.setBackgroundResource(R.drawable.settle_button);
            setUpBody(winlosscomment, true);
        }

        if (activityItem.image_url != null)
            setImage(iconImageView, activityItem.image_url);

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

    private void setUpBody(TextView bodytext, boolean show) {
        if (!show) {
            bodytext.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams lp = bodytext.getLayoutParams();
            lp.height = 0;
            bodytext.setLayoutParams(lp);
        } else {
            bodytext.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = bodytext.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            bodytext.setLayoutParams(lp);
        }

    }

    private void setImage(NetworkImageView imageView, int id) {
        imageView.setImageDrawable(null);
        if (id == R.drawable.ic_notification_avatar) {
            imageView.setBackground(userPic);
        } else
            imageView.setBackgroundResource(id);
    }

    private void setImage(NetworkImageView imageView, String url) {
        //imageView.setImageDrawable(null);
        imageView.setBackgroundResource(R.drawable.ic_notification_avatar);
        imageView.setImageUrl(url, imageLoader);
    }


}

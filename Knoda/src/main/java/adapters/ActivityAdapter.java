package adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import helpers.AdapterHelper;
import models.ActivityItem;
import models.ActivityItemType;
import models.Follow;
import models.Invitation;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import views.activity.ActivityListFollowCell;
import views.activity.ActivityListWinLossCell;
import views.core.MainActivity;
import views.details.DetailsFragment;
import views.group.GroupSettingsFragment;
import views.predictionlists.AnotherUsersProfileFragment;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityAdapter extends PagingAdapter<ActivityItem> {

    static ColorStateList bragcolor;
    static ColorStateList settlecolor;
    static ColorStateList groupcolor;
    public MainActivity activity;
    public BitmapDrawable userPic;
    String filter = "all";
    int pixelToDP = 1;
    RelativeLayout.LayoutParams showButton;
    RelativeLayout.LayoutParams hideButton;

    LinearLayout.LayoutParams showComments;
    LinearLayout.LayoutParams hideComments;

    View.OnClickListener bragClick;
    View.OnClickListener settleClick;
    View.OnClickListener groupClick;

    public ActivityAdapter(Context context, PagingAdapterDatasource<ActivityItem> datasource, ImageLoader imageLoader, final MainActivity activity) {
        super(context, datasource, imageLoader);
        this.activity = activity;
        userPic = (BitmapDrawable) activity.getResources().getDrawable(R.drawable.ic_notification_avatar);

        this.pixelToDP = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1,
                activity.getResources().getDisplayMetrics());

        showButton = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showButton.setMargins(pixelToDP * 16, 0, pixelToDP * 16, pixelToDP * 10);
        showButton.addRule(RelativeLayout.BELOW, R.id.winlosstext_container);

        hideButton = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        hideButton.setMargins(pixelToDP * 16, 0, pixelToDP * 16, 0);

        showComments = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        showComments.setMargins(0, pixelToDP * 10, 0, pixelToDP * 10);

        hideComments = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hideComments.setMargins(0, pixelToDP * 8, 0, pixelToDP * 8);

        bragcolor = activity.getResources().getColorStateList(R.color.brag_selector_text);
        settlecolor = activity.getResources().getColorStateList(R.color.settle_selector_text);
        groupcolor = activity.getResources().getColorStateList(R.color.group_selector_text);

        if (((MainActivity) activity).userManager.getUser() != null)
            imageLoader.get(((MainActivity) activity).userManager.getUser().avatar.small, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    userPic = null;
                    userPic = new BitmapDrawable(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        bragClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainActivity activity1 = (MainActivity) activity;
                activity1.spinner.show();
                activity1.networkingManager.getPrediction(Integer.parseInt((String) v.getTag()), new NetworkCallback<Prediction>() {
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
        };

        settleClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainActivity activity1 = (MainActivity) activity;
                activity1.spinner.show();
                final ActivityItem activityItem = ((ActivityItem) v.getTag());
                activity1.networkingManager.getPrediction(Integer.parseInt(activityItem.target), new NetworkCallback<Prediction>() {
                    @Override
                    public void completionHandler(final Prediction prediction, ServerError error) {
                        activity1.spinner.hide();
                        final Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (prediction == null) {
                                    Toast.makeText(activity1, "Error loading prediction", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                DetailsFragment fragment = DetailsFragment.newInstance(prediction);
                                activity1.pushFragment(fragment);
                            }
                        }, 50);
                    }
                });
            }
        };
        groupClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainActivity activity1 = (MainActivity) activity;
                final ActivityItem activityItem = ((ActivityItem) v.getTag());
                activity1.spinner.show();
                activity1.networkingManager.getInvitationByCode(activityItem.target, new NetworkCallback<Invitation>() {
                    @Override
                    public void completionHandler(Invitation invitation, ServerError error) {
                        activity1.spinner.hide();
                        GroupSettingsFragment fragment = GroupSettingsFragment.newInstance(invitation.group, activityItem.target);
                        activity1.pushFragment(fragment);
                    }
                });
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);
        ActivityItem item = getItem(position);

        if (item.type == ActivityItemType.FOLLOWING) {
            ActivityListFollowCell listItem = (ActivityListFollowCell) AdapterHelper.getConvertViewSafely(convertView, ActivityListFollowCell.class);
            if (listItem == null)
                listItem = (ActivityListFollowCell) LayoutInflater.from(context).inflate(R.layout.list_cell_activity_follows, null);

            listItem.setTag(item);
            updateFollows(listItem, item);
            return listItem;

        } else {
            ActivityListWinLossCell listItem = (ActivityListWinLossCell) AdapterHelper.getConvertViewSafely(convertView, ActivityListWinLossCell.class);
            if (listItem == null)
                listItem = (ActivityListWinLossCell) LayoutInflater.from(context).inflate(R.layout.list_cell_activity_winloss, null);

            listItem.setTag(item);
            listItem.winlossbutton.setTag(item);
            update(listItem, item);
            return listItem;
        }
    }

    private void updateFollows(final ActivityListFollowCell cell, final ActivityItem activityItem) {
        cell.username.setText(activityItem.body);
        cell.title.setText(activityItem.title);
        setImageUrl(cell.iconImageView, activityItem.image_url);
        cell.followbutton.setTag(activityItem.target);
        cell.cover.setVisibility(View.GONE);

        ImageView activityDot = (ImageView) cell.findViewById(R.id.activity_dot);
        if (!activityItem.seen)
            activityDot.setVisibility(View.VISIBLE);
        else
            activityDot.setVisibility(View.INVISIBLE);

        Follow f = activity.helper.checkIfFollowingUser(Integer.parseInt(activityItem.target), activity.myfollowing);
        if (f != null) {
            cell.followbutton.setBackgroundResource(R.drawable.follow_btn_active);
            cell.cover.setTag(true);
            cell.followbutton.setTag(f.id + "");
        } else {
            cell.followbutton.setBackgroundResource(R.drawable.follow_btn);
            cell.cover.setTag(false);
            cell.followbutton.setTag(activityItem.target + "");
        }

        cell.followbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cell.cover.setVisibility(View.VISIBLE);
                cell.followbutton.setEnabled(false);
                activity.followUser(cell.followbutton, cell.cover);
            }
        });

        cell.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserSelected(Integer.parseInt(activityItem.target));
            }
        });

        cell.iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserSelected(Integer.parseInt(activityItem.target));
            }
        });

    }

    private void update(View v, ActivityItem activityItem) {
        NetworkImageView iconImageView = (NetworkImageView) v.findViewById(R.id.winloss_imageview);
        TextView winlosstitle = (TextView) v.findViewById(R.id.winloss_title);
        TextView winlosscomment = (TextView) v.findViewById(R.id.winloss_comment);
        TextView winlossbutton = (TextView) v.findViewById(R.id.winloss_button);
        RelativeLayout buttonContainer = (RelativeLayout) v.findViewById(R.id.winloss_button_container);
        RelativeLayout commentBackground = (RelativeLayout) v.findViewById(R.id.comment_background);
        ImageView activityDot = (ImageView) v.findViewById(R.id.activity_dot);

        setUpCommentBg(commentBackground, false);
        winlossbutton.setOnClickListener(null);
        v.setVisibility(View.VISIBLE);

        if (!activityItem.seen)
            activityDot.setVisibility(View.VISIBLE);
        else
            activityDot.setVisibility(View.INVISIBLE);

        if (activityItem.type == ActivityItemType.COMMENT && (filter.equals("all") || filter.equals("comments"))) {
            setUpCommentBg(commentBackground, true);
            setImage(iconImageView, R.drawable.ic_notification_avatar);
            setUpButton(winlossbutton, buttonContainer, "", false);
            setUpBody(winlosscomment, true);

        } else if (activityItem.type == ActivityItemType.WON && filter.equals("all")) {
            setImage(iconImageView, R.drawable.ic_notification_avatar);
            setUpButton(winlossbutton, buttonContainer, "Brag", activityItem.shareable);
            setUpBody(winlosscomment, true);
            if (!activityItem.title.substring(0, 5).equals("<font"))
                activityItem.title = "<font color='#77BC1F'>You Won</font>" + "—" + activityItem.title;
            winlossbutton.setTextColor(activity.getResources().getColorStateList(R.color.brag_selector_text));
            winlossbutton.setBackgroundResource(R.drawable.brag_selector);
            winlossbutton.setTag(activityItem.target);
            winlossbutton.setOnClickListener(bragClick);

        } else if (activityItem.type == ActivityItemType.LOST && filter.equals("all")) {
            if (!activityItem.title.substring(0, 5).equals("<font"))
                activityItem.title = "<font color='#FE3232'>You Lost</font>" + "—" + activityItem.title;
            setUpButton(winlossbutton, buttonContainer, "", false);
            setUpBody(winlosscomment, true);

        } else if (activityItem.type == ActivityItemType.INVITATION && (filter.equals("all") || filter.equals("invites"))) {
            winlossbutton.setOnClickListener(groupClick);
            setImage(iconImageView, R.drawable.ic_notification_group);
            setUpButton(winlossbutton, buttonContainer, activityItem.body, true);
            winlossbutton.setTextColor(groupcolor);
            winlossbutton.setBackgroundResource(R.drawable.group_selector);
            setUpBody(winlosscomment, false);

        } else if (activityItem.type == ActivityItemType.EXPIRED && (filter.equals("all") || filter.equals("expired"))) {
            winlossbutton.setOnClickListener(settleClick);
            setImage(iconImageView, R.drawable.ic_notification_settle);
            setUpButton(winlossbutton, buttonContainer, "Let's Settle It!", true);
            winlossbutton.setTextColor(settlecolor);
            winlossbutton.setBackgroundResource(R.drawable.settle_selector);
            setUpBody(winlosscomment, true);
        } else {
            v.setVisibility(View.GONE);
        }

        if (activityItem.image_url != null)
            setImageUrl(iconImageView, activityItem.image_url);

        if (activityItem.title != null)
            winlosstitle.setText(Html.fromHtml(activityItem.title));
        if (activityItem.body != null && activityItem.type != ActivityItemType.INVITATION)
            winlosscomment.setText("\"" + activityItem.body + "\"");


    }

    public void setUpButton(TextView winlossbutton, RelativeLayout buttonContainer, String buttontext, boolean show) {
        if (!show) {
            winlossbutton.setVisibility(View.INVISIBLE);
            buttonContainer.setLayoutParams(hideButton);
        } else {
            winlossbutton.setVisibility(View.VISIBLE);
            buttonContainer.setLayoutParams(showButton);
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

    private void setUpCommentBg(RelativeLayout relativeLayout, boolean show) {
        if (!show) {
            relativeLayout.setBackgroundDrawable(null);
            relativeLayout.setLayoutParams(hideComments);
        } else {
            relativeLayout.setBackgroundResource(R.drawable.notification_comment_bg);
            relativeLayout.setLayoutParams(showComments);
        }

    }

    private void setImage(NetworkImageView imageView, int id) {
        imageView.setImageDrawable(null);
        if (id == R.drawable.ic_notification_avatar) {
            imageView.setBackground(userPic);
        } else
            imageView.setBackgroundResource(id);
    }

    private void setImageUrl(NetworkImageView imageView, String url) {
        //imageView.setImageDrawable(null);
        imageView.setBackgroundResource(R.drawable.ic_notification_avatar);
        imageView.setImageUrl(url, imageLoader);
    }

    public String getEmptyString() {
        if (filter.equals("invites"))
            return "Sorry, you don't have any invitations to view.";
        else
            return "Sorry, you don't have any activity to view.";
    }

    public void onUserSelected(int userid) {
        AnotherUsersProfileFragment fragment = AnotherUsersProfileFragment.newInstance(userid);
        activity.pushFragment(fragment);
    }
}

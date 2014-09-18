package views.profile;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import models.User;
import unsorted.BitmapTools;
import views.core.MainActivity;

/**
 * Created by jeff on 9/15/2014.
 */
public class HeadToHeadListCell extends RelativeLayout {

    public TextView win1;
    public TextView wl1;
    public TextView wp1;
    public TextView streak1;
    public ImageView avatar1;
    public TextView win2;
    public TextView wl2;
    public TextView wp2;
    public TextView streak2;
    public ImageView avatar2;
    public TextView username;


    public boolean expanded = true;

    public HeadToHeadListCell(Context context) {
        super(context);
    }

    public HeadToHeadListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        win1 = (TextView) findViewById(R.id.head_to_head_win1);
        wl1 = (TextView) findViewById(R.id.head_to_head_wl1);
        wp1 = (TextView) findViewById(R.id.head_to_head_winp1);
        streak1 = (TextView) findViewById(R.id.head_to_head_streak1);
        avatar1 = (ImageView) findViewById(R.id.head_to_head_avatar1);
        win2 = (TextView) findViewById(R.id.head_to_head_win2);
        wl2 = (TextView) findViewById(R.id.head_to_head_wl2);
        wp2 = (TextView) findViewById(R.id.head_to_head_winp2);
        streak2 = (TextView) findViewById(R.id.head_to_head_streak2);
        avatar2 = (ImageView) findViewById(R.id.head_to_head_avatar2);

        username = (TextView) findViewById(R.id.head_to_head_username);
        findViewById(R.id.head_to_head_container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeExpanded();
            }
        });
        changeExpanded();
    }

    public void setUsers(User user1, User user2, MainActivity mainActivity, int maxBarPixels, int barHeight, int onedp) {
        win1.setText(user2.rivalry.opponent_won + "");
        wl1.setText(user1.won + "-" + user1.lost);
        wp1.setText(user1.winningPercentage + "%");
        setStreak(user1.streak, streak1);
        loadUserPic(user1.avatar.small, avatar1, mainActivity, onedp);

        int barwidth = 0;
        if (user2.rivalry.opponent_won + user2.rivalry.user_won != 0) {
            barwidth = maxBarPixels * user2.rivalry.opponent_won / (user2.rivalry.opponent_won + user2.rivalry.user_won);
            if (user2.rivalry.opponent_won != 0)
                barwidth += 25 * onedp;
        }
        findViewById(R.id.greenBar1).setLayoutParams(new RelativeLayout.LayoutParams(barwidth, barHeight));


        win2.setText(user2.rivalry.user_won + "");
        wl2.setText(user2.won + "-" + user2.lost);
        wp2.setText(user2.winningPercentage + "%");
        setStreak(user2.streak, streak2);
        loadUserPic(user2.avatar.small, avatar2, mainActivity, onedp);
        barwidth = 0;
        if (user2.rivalry.opponent_won + user2.rivalry.user_won != 0) {
            barwidth = maxBarPixels * user2.rivalry.user_won / (user2.rivalry.opponent_won + user2.rivalry.user_won);
            if (user2.rivalry.opponent_won != 0)
                barwidth += 25 * onedp;
        }
        findViewById(R.id.greenBar2).setLayoutParams(new RelativeLayout.LayoutParams(barwidth, barHeight));

        username.setText(user2.username);

        String temp = null;
        Typeface medium = Typeface.create(temp, Typeface.BOLD);
        Typeface normal = Typeface.create("sans-serif-light", Typeface.NORMAL);

        //set bold of higher fields
        if (user1.winningPercentage > user2.winningPercentage) {
            wl1.setTypeface(medium);
            wp1.setTypeface(medium);
            wl2.setTypeface(normal);
            wp2.setTypeface(normal);
        } else if (user1.winningPercentage < user2.winningPercentage) {
            wl1.setTypeface(normal);
            wp1.setTypeface(normal);
            wl2.setTypeface(medium);
            wp2.setTypeface(medium);
        } else {
            wl1.setTypeface(normal);
            wp1.setTypeface(normal);
            wl2.setTypeface(normal);
            wp2.setTypeface(normal);
        }

        switch (streakCompare(user1.streak, user2.streak)) {
            case 0:
                streak1.setTypeface(normal);
                streak2.setTypeface(normal);
                break;
            case 1:
                streak1.setTypeface(medium);
                streak2.setTypeface(normal);
                break;
            case 2:
                streak1.setTypeface(normal);
                streak2.setTypeface(medium);
                break;
        }

    }

    public void setStreak(String streak, TextView streakTV) {
        if (streak == null || streak == "") {
            streakTV.setText("W0");
        } else {
            streakTV.setText(streak);
        }
    }

    //0 means equal, 1 means 1 is better, 2 means 2 is better
    public int streakCompare(String streak1, String streak2) {
        if (streak1.charAt(0) == 'W' && streak2.charAt(0) == 'L')
            return 1;
        if (streak1.charAt(0) == 'L' && streak2.charAt(0) == 'W')
            return 2;

        if (streak1.charAt(0) == 'W' && streak2.charAt(0) == 'W') {
            int x1 = Integer.parseInt(streak1.substring(1));
            int x2 = Integer.parseInt(streak2.substring(1));
            if (x1 > x2)
                return 1;
            else if (x1 < x2)
                return 2;
            else
                return 0;
        }

        if (streak1.charAt(0) == 'L' && streak2.charAt(0) == 'L') {
            int x1 = Integer.parseInt(streak1.substring(1));
            int x2 = Integer.parseInt(streak2.substring(1));
            if (x1 < x2)
                return 1;
            else if (x1 > x2)
                return 2;
            else
                return 0;
        }
        return 0;
    }


    public void changeExpanded() {
        if (expanded) {
            findViewById(R.id.head_to_head_stats_container).setVisibility(GONE);
            expanded = false;
        } else {
            findViewById(R.id.head_to_head_stats_container).setVisibility(VISIBLE);
            expanded = true;
        }
    }


    private void loadUserPic(final String url, final ImageView imageView, final MainActivity mainActivity, final int onedp) {
        mainActivity.networkingManager.getImageLoader().get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response == null || response.getBitmap() == null) {
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadUserPic(url, imageView, mainActivity, onedp);
                        }
                    }, 100);
                } else {
                    imageView.setImageBitmap(BitmapTools.getclipSized(response.getBitmap(), onedp * 50, onedp * 50));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

}

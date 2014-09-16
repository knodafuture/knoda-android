package views.profile;

import android.content.Context;
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

    }

    public void setStreak(String streak, TextView streakTV) {
        if (streak == null || streak == "") {
            streakTV.setText("W0");
        } else {
            streakTV.setText(streak);
        }
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

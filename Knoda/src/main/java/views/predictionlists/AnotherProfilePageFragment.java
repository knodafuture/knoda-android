package views.predictionlists;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by jeffcailteux on 9/17/14.
 */
public class AnotherProfilePageFragment extends Fragment {
    int x;
    MainActivity mainActivity;
    int barHeight, maxBarPixels, onedp;
    User user;

    public AnotherProfilePageFragment() {

    }

    public static AnotherProfilePageFragment newInstance(int x, int barheight, int maxbarpixels, int onedp, User user, MainActivity mainActivity) {
        AnotherProfilePageFragment fragment = new AnotherProfilePageFragment();
        fragment.x = x;
        fragment.mainActivity = mainActivity;
        fragment.barHeight = barheight;
        fragment.user = user;
        fragment.maxBarPixels = maxbarpixels;
        fragment.onedp = onedp;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = new View(getActivity());

        if (x == 1) {
            v = inflater.inflate(R.layout.view_profile_stats, container, false);
            TextView pointsTextView = (TextView) v.findViewById(R.id.profile_points);
            TextView winPercentTextView = (TextView) v.findViewById(R.id.profile_winpercent);
            TextView streakTextView = (TextView) v.findViewById(R.id.profile_winstreak);
            TextView winLossTextView = (TextView) v.findViewById(R.id.profile_winloss);

            pointsTextView.setText(user.points.toString());
            winLossTextView.setText(user.won.toString() + "-" + user.lost.toString());
            setStreak(user.streak, streakTextView);
            winPercentTextView.setText(user.winningPercentage.toString() + "%");

        } else if (x == 0) {
            v = inflater.inflate(R.layout.view_profile_head_to_head, container, false);
            ImageView avatar1 = (ImageView) v.findViewById(R.id.profile_avatar1);
            ImageView avatar2 = (ImageView) v.findViewById(R.id.profile_avatar2);

            try {
                loadUserPic(mainActivity.userManager.getUser().avatar.small, avatar1);
                loadUserPic(user.avatar.small, avatar2);
            } catch (Exception ignored) {
            }

            if (user.rivalry != null) {
                ((TextView) v.findViewById(R.id.head_to_head_win1)).setText(user.rivalry.opponent_won + "");
                ((TextView) v.findViewById(R.id.head_to_head_win2)).setText(user.rivalry.user_won + "");

                int barwidth = 0;
                if (user.rivalry.opponent_won + user.rivalry.user_won != 0) {
                    barwidth = maxBarPixels * user.rivalry.opponent_won / (user.rivalry.opponent_won + user.rivalry.user_won);
                    if (user.rivalry.opponent_won != 0)
                        barwidth += 25 * onedp;
                }
                v.findViewById(R.id.greenBar1).setLayoutParams(new RelativeLayout.LayoutParams(barwidth, barHeight));

                barwidth = 0;
                if (user.rivalry.opponent_won + user.rivalry.user_won != 0) {
                    barwidth = maxBarPixels * user.rivalry.user_won / (user.rivalry.opponent_won + user.rivalry.user_won);
                    if (user.rivalry.user_won != 0)
                        barwidth += 25 * onedp;
                }
                v.findViewById(R.id.greenBar2).setLayoutParams(new RelativeLayout.LayoutParams(barwidth, barHeight));
            }
        }
        return v;
    }

    public void setStreak(String streak, TextView streakTV) {
        if (streak == null || streak.equals("")) {
            streakTV.setText("W0");
        } else {
            streakTV.setText(streak);
        }
    }

    private void loadUserPic(final String url, final ImageView imageView) {
        mainActivity.networkingManager.getImageLoader().get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response == null || response.getBitmap() == null) {
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadUserPic(url, imageView);
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


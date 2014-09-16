package adapters;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
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
import unsorted.Logger;
import views.core.MainActivity;

public class AnotherProfilePagerAdapter extends FragmentStatePagerAdapter {
    User user;
    Context context;
    MainActivity mainActivity;

    int maxBarPixels = 0;
    int barHeight = 0;
    int onedp;

    public AnotherProfilePagerAdapter(MainActivity mainActivity, User u, Context c) {
        super(mainActivity.getFragmentManager());
        Logger.log("Activity Page created");
        this.mainActivity = mainActivity;
        user = u;
        context = c;

        onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        Display display = mainActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Point size = new Point();
        display.getSize(size);

        maxBarPixels = (size.x / 2) - (45 * onedp);
        barHeight = 35 * onedp;

    }

    @Override
    public Fragment getItem(int id) {
        return new AnotherProfilePageFragment(id);
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void setStreak(String streak, TextView streakTV) {
        if (streak == null || streak == "") {
            streakTV.setText("W0");
        } else {
            streakTV.setText(streak);
        }
    }

    public class AnotherProfilePageFragment extends Fragment {
        int x;

        public AnotherProfilePageFragment(int x) {
            this.x = x;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View v = new View(getActivity());

            if (x == 0) {
                v = inflater.inflate(R.layout.view_profile_stats, container, false);
                TextView pointsTextView = (TextView) v.findViewById(R.id.profile_points);
                TextView winPercentTextView = (TextView) v.findViewById(R.id.profile_winpercent);
                TextView streakTextView = (TextView) v.findViewById(R.id.profile_winstreak);
                TextView winLossTextView = (TextView) v.findViewById(R.id.profile_winloss);

                pointsTextView.setText(user.points.toString());
                winLossTextView.setText(user.won.toString() + "-" + user.lost.toString());
                setStreak(user.streak, streakTextView);
                winPercentTextView.setText(user.winningPercentage.toString() + "%");

            } else if (x == 1) {
                v = inflater.inflate(R.layout.view_profile_head_to_head, container, false);
                ImageView avatar1 = (ImageView) v.findViewById(R.id.profile_avatar1);
                ImageView avatar2 = (ImageView) v.findViewById(R.id.profile_avatar2);

                //avatar1.setImageUrl(mainActivity.userManager.getUser().avatar.small, mainActivity.networkingManager.getImageLoader());
                loadUserPic(mainActivity.userManager.getUser().avatar.small, avatar1);
                loadUserPic(user.avatar.small, avatar2);

                //avatar2.setImageUrl(user.avatar.small, mainActivity.networkingManager.getImageLoader());

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

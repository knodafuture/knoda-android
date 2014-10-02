package adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import java.util.Random;

import helpers.AdapterHelper;
import models.Prediction;
import models.User;
import views.core.MainActivity;
import views.profile.FollowFragment;
import views.profile.UserProfileHeaderView;

/**
 * Created by nick on 2/3/14.
 */
public class AnotherUsersProfileAdapter extends PredictionAdapter {


    public User user;
    private MainActivity mainActivity;
    private ViewPager mViewPager;

    public AnotherUsersProfileAdapter(Context context, PagingAdapterDatasource<Prediction> datasource, ImageLoader imageLoader, MainActivity mainActivity) {
        super(context, datasource, imageLoader, mainActivity.bus, true);
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        if (user == null)
            return super.getCount();

        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (user == null)
            return super.getView(position, convertView, parent);

        if (position == 0)
            return getHeaderView(convertView);

        return super.getView(position - 1, convertView, parent);
    }

    public void setUser(User user) {
        this.user = user;
        notifyDataSetChanged();
    }

    View getHeaderView(final View convertView) {

        UserProfileHeaderView header = (UserProfileHeaderView) AdapterHelper.getConvertViewSafely(convertView, UserProfileHeaderView.class);

        if (header == null)
            header = new UserProfileHeaderView(context);

        TextView tv_followers = (TextView) header.findViewById(R.id.profile_followers);
        TextView tv_following = (TextView) header.findViewById(R.id.profile_following);
        View followers_container = header.findViewById(R.id.profile_followers_container);
        View following_container = header.findViewById(R.id.profile_following_container);
        NetworkImageView avatarImageView = (NetworkImageView) header.findViewById(R.id.profile_avatar);

        final ImageView whitedot1 = (ImageView) header.findViewById(R.id.whitedot1);
        final ImageView whitedot2 = (ImageView) header.findViewById(R.id.whitedot2);

        tv_followers.setText(user.follower_count + "");
        tv_following.setText(user.following_count + "");

        if (mViewPager != null) {
            header.removeView(mViewPager);
        }
        mViewPager = new ViewPager(context);
        mViewPager.setId(2000 + new Random().nextInt(100));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position, true);
                if (position == 0) {
                    whitedot1.setAlpha(1f);
                    whitedot2.setAlpha(.5f);
                } else {
                    whitedot1.setAlpha(.5f);
                    whitedot2.setAlpha(1f);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(new AnotherProfilePagerAdapter(mainActivity, user, context));
        ((LinearLayout) header.findViewById(R.id.pagerContainer)).addView(mViewPager);
        if (user.rivalry.user_won == 0 && user.rivalry.opponent_won == 0)
            mViewPager.setCurrentItem(1);
        else
            mViewPager.setCurrentItem(0);

        if (avatarImageView != null && user.avatar != null && user.avatar.big != null)
            avatarImageView.setImageUrl(user.avatar.big, imageLoader);

        following_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowFragment followFragment = FollowFragment.newInstance(1, user);
                mainActivity.pushFragment(followFragment);
            }
        });
        followers_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowFragment followFragment = FollowFragment.newInstance(0, user);
                mainActivity.pushFragment(followFragment);
            }
        });

        return header;
    }


}

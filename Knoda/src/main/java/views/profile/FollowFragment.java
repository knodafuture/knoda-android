package views.profile;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.Random;

import butterknife.InjectView;
import models.User;
import unsorted.Logger;
import unsorted.PagerSlidingTabStrip;
import views.core.BaseFragment;

public class FollowFragment extends BaseFragment {
    View topview;
    FollowPagerAdapter adapter;
    User user;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    int startTab = 0;
    private ViewPager mViewPager;

    public FollowFragment() {
    }

    public static FollowFragment newInstance(int position, User user) {
        FollowFragment fragment = new FollowFragment();
        fragment.user = user;
        fragment.startTab = position;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contestleaderboard, container, false);
        topview = view;
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        FlurryAgent.logEvent("Follow Feed");
        setTitle(user.username.toUpperCase());

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getActivity().getResources().getDisplayMetrics());
        final int onesp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getActivity().getResources().getDisplayMetrics());
        Point size = new Point();
        display.getSize(size);
        int screenwidth = size.x;

        tabs.setIndicatorHeight(onedp * 4);
        tabs.setTextSize(onesp * 16);
        tabs.setBackgroundResource(R.color.knodaLightGreen);

        tabs.setTabWidth((int) (screenwidth * 1.0f) / 2);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mViewPager != null) {
            ((LinearLayout) topview).removeView(mViewPager);
        }
        mViewPager = new ViewPager(getActivity().getApplicationContext());
        mViewPager.setId(2000 + new Random().nextInt(100));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ((LinearLayout) topview).addView(mViewPager);
        adapter = new FollowPagerAdapter(getFragmentManager(), user);
        mViewPager.setAdapter(adapter);
        tabs.setViewPager(mViewPager);
        mViewPager.setCurrentItem(startTab, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }


    public class FollowPagerAdapter extends FragmentStatePagerAdapter {
        User user;

        public FollowPagerAdapter(FragmentManager fm, User user) {
            super(fm);
            this.user = user;
            Logger.log("Follow Feed created");
        }

        @Override
        public Fragment getItem(int id) {
            return FollowFeedFragment.newInstance(id, user);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Followers";
            else
                return "Following";
        }
    }

}
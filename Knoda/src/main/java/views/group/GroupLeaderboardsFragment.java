package views.group;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.knoda.knoda.R;

import java.util.Random;

import adapters.LeaderboardPagerAdapter;
import butterknife.InjectView;
import factories.GsonF;
import models.Group;
import pubsub.ChangeGroupEvent;
import unsorted.PagerSlidingTabStrip;
import views.core.BaseFragment;

public class GroupLeaderboardsFragment extends BaseFragment {
    public Group group;
    private ViewPager mViewPager;
    private View view;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    public GroupLeaderboardsFragment() {
    }

    public static GroupLeaderboardsFragment newInstance(Group group) {
        GroupLeaderboardsFragment fragment = new GroupLeaderboardsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP", GsonF.actory().toJson(group));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
        bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_leaderboards, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(group.name.toUpperCase());
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getActivity().getResources().getDisplayMetrics());
        final int onesp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getActivity().getResources().getDisplayMetrics());
        Point size = new Point();
        display.getSize(size);
        tabs.setIndicatorHeight(onedp * 4);
        tabs.setTextSize(onesp * 16);
        tabs.setBackgroundResource(R.color.knodaLightGreen);
        tabs.setTabWidth((int) (size.x * 1.0f / 3));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new ChangeGroupEvent(group));
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.groups_leaderboards_container);
        if (mViewPager != null) {
            ll.removeView(mViewPager);
        }
        mViewPager = new ViewPager(getActivity().getApplicationContext());
        mViewPager.setId(2000 + group.id + new Random().nextInt(100));
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
        ll.addView(mViewPager);
        FragmentStatePagerAdapter adapter = new LeaderboardPagerAdapter(getFragmentManager(), group);
        mViewPager.setAdapter(adapter);
        tabs.setViewPager(mViewPager);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.post(new ChangeGroupEvent(null));
    }

}

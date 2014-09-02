package views.contests;

import android.graphics.Point;
import android.os.Bundle;
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

import adapters.ContestStagesPagerAdapter;
import butterknife.InjectView;
import models.Contest;
import models.ContestStage;
import unsorted.PagerSlidingTabStrip;
import views.core.BaseFragment;

public class ContestLeaderboardFragment extends BaseFragment {
    View topview;
    ContestStagesPagerAdapter adapter;
    Contest contest;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    private ViewPager mViewPager;

    public ContestLeaderboardFragment() {
    }

    public static ContestLeaderboardFragment newInstance(Contest contest) {
        ContestLeaderboardFragment fragment = new ContestLeaderboardFragment();
        fragment.contest = contest;
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
        FlurryAgent.logEvent("Contest Leaderboard");
        setTitle("LEADERBOARD");

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getActivity().getResources().getDisplayMetrics());
        final int onesp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getActivity().getResources().getDisplayMetrics());
        Point size = new Point();
        display.getSize(size);
        //int screenwidth = size.x;

        tabs.setIndicatorHeight(onedp * 4);
        tabs.setTextSize(onesp * 16);
        tabs.setBackgroundResource(R.color.knodaLightGreen);

        if (contest.contestStages.size() > 0 && contest.contestStages.get(0).name.equals("Overall")) {
            //dont add overall again
        } else {
            ContestStage overall = new ContestStage();
            overall.id = -1;
            overall.contest_id = contest.id;
            overall.name = "Overall";
            overall.sort_order = 0;
            contest.contestStages.add(0, overall);
        }

//        if (contest.contestStages.size() > 2) {
//            tabs.setTabWidth((int) (screenwidth * 1.0f / 3));
//        } else {
//            tabs.setTabWidth((int) (screenwidth * 1.0f) / contest.contestStages.size());
//        }

        if (contest.contestStages.size() == 1)
            tabs.setVisibility(View.GONE);
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
        adapter = new ContestStagesPagerAdapter(getFragmentManager(), contest);
        mViewPager.setAdapter(adapter);
        tabs.setViewPager(mViewPager);
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

}
package views.contests;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.Random;

import adapters.ContestStagesPagerAdapter;
import butterknife.InjectView;
import models.Contest;
import models.ContestStage;
import unsorted.PagerSlidingTabStrip;
import views.core.BaseFragment;

public class ContestLeaderboardFragment extends BaseFragment {
    String filter = "all";
    View topview;
    ContestStagesPagerAdapter adapter;
    Contest contest;
    //@InjectView(R.id.tabContainer)
    //LinearLayout tabContainer;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    LinearLayout.LayoutParams lp;
    RelativeLayout.LayoutParams lpTV = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    RelativeLayout.LayoutParams lpUnder;
    ArrayList<TextView> tabTV = new ArrayList<TextView>();
    ArrayList<View> tabUnderline = new ArrayList<View>();
    private ViewPager mViewPager;
    int lastPosition = 0;

    public ContestLeaderboardFragment() {
    }

    public static ContestLeaderboardFragment newInstance(Contest contest) {
        ContestLeaderboardFragment fragment = new ContestLeaderboardFragment();
        fragment.contest = contest;
        return fragment;
    }

    private void changeFilter(int number) {

        //clear all tabs colors
        for (int i = 0; i != 3; i++) {
            tabTV.get(i).setTextColor(getResources().getColor(R.color.knodaLighterGreen));
            tabUnderline.get(i).setVisibility(View.INVISIBLE);
            tabTV.get(i).setText("");
        }

        //set color of right tab
        if (number == contest.contestStages.size() - 1) {
            if (contest.contestStages.size() > 2) {
                tabTV.get(2).setTextColor(Color.WHITE);
                tabUnderline.get(2).setVisibility(View.VISIBLE);
                tabTV.get(2).setText(contest.contestStages.get(number).name);
                tabTV.get(1).setText(contest.contestStages.get(number - 1).name);
                tabTV.get(0).setText(contest.contestStages.get(number - 2).name);
            } else {
                tabTV.get(number).setTextColor(Color.WHITE);
                tabUnderline.get(number).setVisibility(View.VISIBLE);
                tabTV.get(number).setText(contest.contestStages.get(number).name);
                if (number - 1 >= 0)
                    tabTV.get(number - 1).setText(contest.contestStages.get(number - 1).name);
            }
        } else if (number == 0) {
            if (contest.contestStages.size() > 0) {
                tabTV.get(0).setTextColor(Color.WHITE);
                tabUnderline.get(0).setVisibility(View.VISIBLE);
                tabTV.get(0).setText(contest.contestStages.get(number).name);
                if (number + 1 < contest.contestStages.size())
                    tabTV.get(1).setText(contest.contestStages.get(number + 1).name);
                if (number + 2 < contest.contestStages.size())
                    tabTV.get(2).setText(contest.contestStages.get(number + 2).name);
            }
        } else {
            tabTV.get(1).setTextColor(Color.WHITE);
            tabUnderline.get(1).setVisibility(View.VISIBLE);
            tabTV.get(0).setText(contest.contestStages.get(number - 1).name);
            tabTV.get(1).setText(contest.contestStages.get(number).name);
            tabTV.get(2).setText(contest.contestStages.get(number + 1).name);
        }
        lastPosition = number;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        setHasOptionsMenu(true);
        lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lpTV.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0, 0);
        lpUnder = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

        lpUnder.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
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
        int screenwidth = size.x;

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

        if (contest.contestStages.size() > 2) {
            tabs.setTabWidth((int) (screenwidth * 1.0f / 3));
        } else {
            tabs.setTabWidth((int) (screenwidth * 1.0f) / contest.contestStages.size());
        }
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
                //changeFilter(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ((LinearLayout) topview).addView(mViewPager);
        adapter = new ContestStagesPagerAdapter(getFragmentManager(), contest);
        mViewPager.setAdapter(adapter);
        tabs.setViewPager(mViewPager);
        //changeFilter(0);
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

    private void tabClick(int x) {
        Log.i("tab", "Last position: " + lastPosition);
        Log.i("tab", "x: " + x);
        if (lastPosition == 0) {
            Log.i("tab", "first");
            mViewPager.setCurrentItem(x, true);
            //changeFilter(x);
        } else if (lastPosition == contest.contestStages.size() - 1) {
            Log.i("tab", "last");
            if (contest.contestStages.size() == 2) {
                mViewPager.setCurrentItem(lastPosition + x - 1, true);
                //changeFilter(lastPosition + x - 1);
            } else {
                mViewPager.setCurrentItem(lastPosition + x - 2, true);
                //changeFilter(lastPosition + x - 2);
            }
        } else {
            Log.i("tab", "middle");
            if (x != 1) {
                mViewPager.setCurrentItem(lastPosition + x - 1);
                //changeFilter(lastPosition + x - 1);
            }
        }
    }

}
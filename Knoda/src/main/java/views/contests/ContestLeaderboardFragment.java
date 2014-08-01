package views.contests;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
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
import views.core.BaseFragment;

public class ContestLeaderboardFragment extends BaseFragment {
    String filter = "all";
    View topview;
    ContestStagesPagerAdapter adapter;
    private ViewPager mViewPager;
    Contest contest;
    @InjectView(R.id.tabContainer)
    LinearLayout tabContainer;

    LinearLayout.LayoutParams lp;
    RelativeLayout.LayoutParams lpTV = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    RelativeLayout.LayoutParams lpUnder;
    ArrayList<TextView> tabTV = new ArrayList<TextView>();
    ArrayList<View> tabUnderline = new ArrayList<View>();

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
            tabTV.get(0).setTextColor(Color.WHITE);
            tabUnderline.get(0).setVisibility(View.VISIBLE);
            tabTV.get(0).setText(contest.contestStages.get(number).name);
            if (number + 1 < contest.contestStages.size())
                tabTV.get(1).setText(contest.contestStages.get(number + 1).name);
            if (number + 2 < contest.contestStages.size())
                tabTV.get(2).setText(contest.contestStages.get(number + 2).name);
        } else {
            tabTV.get(1).setTextColor(Color.WHITE);
            tabUnderline.get(1).setVisibility(View.VISIBLE);
            tabTV.get(0).setText(contest.contestStages.get(number - 1).name);
            tabTV.get(1).setText(contest.contestStages.get(number).name);
            tabTV.get(2).setText(contest.contestStages.get(number + 1).name);
        }

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
        ContestStage overall = new ContestStage();
        overall.id = -1;
        overall.contest_id = contest.id;
        overall.name = "Overall";
        overall.sort_order = 0;
        contest.contestStages.add(0, overall);

        tabTV.add((TextView) getView().findViewById(R.id.activity_1));
        tabTV.add((TextView) getView().findViewById(R.id.activity_2));
        tabTV.add((TextView) getView().findViewById(R.id.activity_3));
        tabUnderline.add(getView().findViewById(R.id.underline_1));
        tabUnderline.add(getView().findViewById(R.id.underline_2));
        tabUnderline.add(getView().findViewById(R.id.underline_3));

        if (contest.contestStages.size() == 1) {
            lp.weight = 1f;
            getView().findViewById(R.id.container_1).setLayoutParams(lp);
        } else if (contest.contestStages.size() == 2) {
            lp.weight = .5f;
            getView().findViewById(R.id.container_1).setLayoutParams(lp);
            getView().findViewById(R.id.container_2).setLayoutParams(lp);
        } else
            lp.weight = .333f;

        if (contest.contestStages.size() > 0) {
            tabTV.get(0).setText(contest.contestStages.get(0).name);
        } else {
            tabTV.get(0).setVisibility(View.GONE);
            tabUnderline.get(0).setVisibility(View.GONE);
        }
        if (contest.contestStages.size() > 1) {
            tabTV.get(1).setText(contest.contestStages.get(1).name);
        } else {
            tabTV.get(1).setVisibility(View.GONE);
            tabUnderline.get(1).setVisibility(View.GONE);
        }
        if (contest.contestStages.size() > 2) {
            tabTV.get(2).setText(contest.contestStages.get(2).name);
        } else {
            tabTV.get(2).setVisibility(View.GONE);
            tabUnderline.get(2).setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mViewPager != null) {
            ((LinearLayout) topview).removeView(mViewPager);
        }
        mViewPager = new ViewPager(getActivity().getApplicationContext());
        mViewPager.setId(2000 + new Random().nextInt(100));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFilter(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ((LinearLayout) topview).addView(mViewPager);
        adapter = new ContestStagesPagerAdapter(getFragmentManager(), contest);
        mViewPager.setAdapter(adapter);
        changeFilter(0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onDestroyView() {
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroyView();
    }

}
package views.contests;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
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
import views.core.CustomTab;

public class ContestLeaderboardFragment extends BaseFragment {
    String filter = "all";
    View topview;
    ContestStagesPagerAdapter adapter;
    private ViewPager mViewPager;
    Contest contest;
    @InjectView(R.id.tabContainer)
    LinearLayout tabContainer;
    ArrayList<CustomTab> tabs = new ArrayList<CustomTab>();
    CustomTab selected;

    LinearLayout.LayoutParams lp;
    RelativeLayout.LayoutParams lpTV = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    RelativeLayout.LayoutParams lpUnder;


    public ContestLeaderboardFragment() {
    }

    public static ContestLeaderboardFragment newInstance(Contest contest) {
        ContestLeaderboardFragment fragment = new ContestLeaderboardFragment();
        fragment.contest = contest;
        return fragment;
    }

    private void changeFilter(int number) {
        if (tabs.get(number) == selected || number > tabs.size() - 1)
            return;
        if (selected != null) {
            selected.text.setTextColor(getResources().getColor(R.color.knodaLighterGreen));
            selected.underline.setVisibility(View.INVISIBLE);
        }
        selected = tabs.get(number);
        selected.text.setTextColor(Color.WHITE);
        selected.underline.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        setHasOptionsMenu(true);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //lp = new LinearLayout.LayoutParams((int) (width / 3), ViewGroup.LayoutParams.MATCH_PARENT);
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        lp.weight = 1;
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
        for (ContestStage cs : contest.contestStages) {
            addTab(cs);
        }
    }

    public void addTab(ContestStage contestStage) {
        CustomTab customTab = new CustomTab(getActivity());
        customTab.setLayoutParams(lp);

        TextView textView = new TextView(getActivity());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.knodaLighterGreen));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setLayoutParams(lpTV);
        textView.setText(contestStage.name);
        customTab.text = textView;
        customTab.addView(textView);

        View view = new View(getActivity());
        view.setBackgroundColor(getResources().getColor(R.color.knodaDarkGreen));
        view.setLayoutParams(lpUnder);
        view.setVisibility(View.INVISIBLE);
        customTab.underline = view;
        customTab.addView(view);
        customTab.setTag(contestStage);
        customTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFilter(((ContestStage) v.getTag()).sort_order);
                mViewPager.setCurrentItem((((ContestStage) v.getTag()).sort_order));
            }
        });

        tabs.add(customTab);
        tabContainer.addView(customTab);
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
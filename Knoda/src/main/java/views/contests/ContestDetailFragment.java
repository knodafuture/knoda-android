package views.contests;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.Random;

import adapters.ContestDetailPagerAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import models.Contest;
import views.core.BaseFragment;
import views.core.BaseWebFragment;

public class ContestDetailFragment extends BaseFragment {

    public boolean loaded = false;
    TextView selectedFilter;
    View selectedUnderline;
    ContestDetailPagerAdapter adapter;
    @InjectView(R.id.topview)
    LinearLayout topview;
    @InjectView(R.id.contest_detail_header)
    RelativeLayout header;
    private ViewPager mViewPager;
    Contest contest;
    LinearLayout.LayoutParams params;
    int topContainerHeight;

    public static ContestDetailFragment newInstance(Contest contest) {
        ContestDetailFragment fragment = new ContestDetailFragment();
        fragment.contest = contest;
        return fragment;
    }

    @OnClick(R.id.activity_1)
    void onClick1() {
        mViewPager.setCurrentItem(0, true);
    }

    @OnClick(R.id.activity_2)
    void onClick2() {
        mViewPager.setCurrentItem(1, true);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_detail, container, false);
        ButterKnife.inject(this, view);
        getActivity().invalidateOptionsMenu();

        selectedFilter = (TextView) view.findViewById(R.id.activity_1);
        selectedUnderline = view.findViewById(R.id.underline_1);
        setTitle("DETAILS");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ContestListCell listItem = (ContestListCell) LayoutInflater.from(getActivity()).inflate(R.layout.list_cell_contest, null);
        listItem.setContest(contest);
        listItem.setHeaderMode();
        header.addView(listItem);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        FlurryAgent.logEvent("ContestDetail_Screen");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mViewPager != null) {
            topview.removeView(mViewPager);
        }
        mViewPager = new ViewPager(getActivity().getApplicationContext());
        mViewPager.setId(2000 + new Random().nextInt(100));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int filterId = 0;
                switch (position) {
                    case 0:
                        filterId = R.id.activity_1;
                        break;
                    case 1:
                        filterId = R.id.activity_2;
                        break;
                }
                changeFilter(filterId);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        topview.addView(mViewPager);
        adapter = new ContestDetailPagerAdapter(getFragmentManager(), contest.id, this);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);

        header.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                topContainerHeight = header.getHeight();
                params = (LinearLayout.LayoutParams) header.getLayoutParams();
                header.removeOnLayoutChangeListener(this);
                loaded = true;
            }
        });

    }

    @Override
    public void onPause() {
        loaded = false;
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.contestdetails, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void changeFilter(int id) {
        selectedUnderline.setVisibility(View.INVISIBLE);
        switch (id) {
            case R.id.activity_1:
                selectedUnderline = topview.findViewById(R.id.underline_1);
                break;
            case R.id.activity_2:
                selectedUnderline = topview.findViewById(R.id.underline_2);
                break;
        }
        selectedFilter.setTextColor(getResources().getColor(R.color.knodaLighterGreen));
        selectedFilter = ((TextView) topview.findViewById(id));
        selectedFilter.setTextColor(Color.WHITE);
        selectedUnderline.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contest_info: {
                pushFragment(BaseWebFragment.newInstance(contest.detail_url, "DETAILS", false));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

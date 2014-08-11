package views.contests;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import views.core.CustomViewPager;
import views.core.MainActivity;

public class ContestDetailFragment extends BaseFragment {

    public boolean loaded = false;
    TextView selectedFilter;
    View selectedUnderline;
    ContestDetailPagerAdapter adapter;
    @InjectView(R.id.topview)
    LinearLayout topview;
    @InjectView(R.id.contest_detail_header)
    RelativeLayout header;
    Contest contest;
    LinearLayout.LayoutParams params;
    int topContainerHeight;
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    View walkthrough1 = null;
    private CustomViewPager mViewPager;

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
        sharedPrefManager.setShouldShowContestVotingWalkthrough(true);
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
        listItem.setContest(contest, (MainActivity) getActivity());
        listItem.setHeaderMode();

        LinearLayout.LayoutParams title_normal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams title_no_image = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getActivity().getResources().getDisplayMetrics());
        title_no_image.setMargins(onedp * 15, onedp * 15, onedp * 15, 0);
        title_normal.setMargins(onedp * 15, onedp * 5, onedp * 15, 0);

        if (contest.avatar != null) {
            listItem.avatarImageView.setImageUrl(contest.avatar.big, networkingManager.getImageLoader());
            listItem.titleTV.setLayoutParams(title_normal);
        } else {
            listItem.findViewById(R.id.contest_avatar_container).setVisibility(View.GONE);
            listItem.titleTV.setLayoutParams(title_no_image);
        }

        header.addView(listItem);

        if (sharedPrefManager.shouldShowContestVotingWalkthrough() && contest.contestMyInfo == null) {
            final android.os.Handler h = new android.os.Handler();
            final View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_contest_predict_walkthrough, null);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slidein);
            v.startAnimation(fadeInAnimation);
            header.addView(v);
            header.setLayoutParams(lp);
            walkthrough1 = v;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    hidePredictWalkthrough();
                }
            });
        }

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        FlurryAgent.logEvent("ContestDetail_Screen");
    }

    public void hidePredictWalkthrough() {
        if (walkthrough1 != null) {
            sharedPrefManager.setShouldShowContestVotingWalkthrough(false);
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeoutshrink);
            walkthrough1.startAnimation(fadeOutAnimation);
            walkthrough1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    walkthrough1.setVisibility(View.INVISIBLE);
                    walkthrough1 = null;
                    addVotedWalkthrough();
                }
            }, 500);
        }
    }

    private void addVotedWalkthrough() {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_contest_voted_walkthrough, null);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slidein);
        v.startAnimation(fadeInAnimation);
        final Handler h = new Handler();
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeoutshrink);
                v.startAnimation(fadeOutAnimation);
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.GONE);
                    }
                }, 500);
            }
        });
        ((RelativeLayout) getView().findViewById(R.id.contest_walkthrough_container)).addView(v);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mViewPager != null) {
            topview.removeView(mViewPager);
        }
        mViewPager = new CustomViewPager(getActivity().getApplicationContext());
        mViewPager.setPagingEnabled(false);
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

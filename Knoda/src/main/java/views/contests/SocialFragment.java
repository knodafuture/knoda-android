package views.contests;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.Random;

import adapters.SocialPagerAdapter;
import butterknife.OnClick;
import views.core.BaseFragment;
import views.core.MainActivity;

public class SocialFragment extends BaseFragment {
    String filter = "all";
    TextView selectedFilter;
    View selectedUnderline;
    View topview;
    SocialPagerAdapter adapter;
    private ViewPager mViewPager;


    public SocialFragment() {
    }

    public static SocialFragment newInstance() {
        SocialFragment fragment = new SocialFragment();
        return fragment;
    }

    @OnClick(R.id.activity_1)
    void onClickAll() {
        mViewPager.setCurrentItem(0, true);
    }

    @OnClick(R.id.activity_2)
    void onClickExpired() {
        mViewPager.setCurrentItem(1, true);
    }

    private void changeFilter(int id) {
        selectedUnderline.setVisibility(View.INVISIBLE);
        switch (id) {
            case R.id.activity_1:
                filter = "all";
                selectedUnderline = topview.findViewById(R.id.underline_1);
                break;
            case R.id.activity_2:
                filter = "expired";
                selectedUnderline = topview.findViewById(R.id.underline_2);
                break;
        }
        sharedPrefManager.setSavedActivityFilter(id);
        selectedFilter.setTextColor(getResources().getColor(R.color.knodaLighterGreen));
        selectedFilter = ((TextView) topview.findViewById(id));
        selectedFilter.setTextColor(Color.WHITE);
        selectedUnderline.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        sharedPrefManager.setSavedActivityFilter(R.id.activity_1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_social, container, false);
        topview = view;
        selectedFilter = (TextView) view.findViewById(R.id.activity_1);
        selectedUnderline = view.findViewById(R.id.underline_1);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("SocialFeed");
        setTitle("GROUPS");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).resetNavIcons();
        getActivity().findViewById(R.id.nav_groups).setBackgroundResource(R.drawable.nav_groups_active);
        ((TextView) getActivity().findViewById(R.id.nav_groups_text)).setTextColor(Color.parseColor("#EFEFEF"));
        changeFilter(sharedPrefManager.getSavedActivityFilter());


        if (mViewPager != null) {
            ((LinearLayout) topview.findViewById(R.id.social_container)).removeView(mViewPager);
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
                adapter.getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        ((LinearLayout) topview.findViewById(R.id.social_container)).addView(mViewPager);
        adapter = new SocialPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(adapter);
        switch (sharedPrefManager.getSavedActivityFilter()) {
            case R.id.activity_1:
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.activity_2:
                mViewPager.setCurrentItem(1, false);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
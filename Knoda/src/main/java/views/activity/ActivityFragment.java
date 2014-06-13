package views.activity;

import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import javax.inject.Inject;

import butterknife.OnClick;
import pubsub.ActivitiesViewedEvent;
import views.core.BaseFragment;
import adapters.ActivityPagerAdapter;

public class ActivityFragment extends BaseFragment {

    private ViewPager mViewPager;
    private View view;

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    @Inject
    public ActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
    }

    @OnClick(R.id.activity_1)
    public void on1Click() {
        mViewPager.setCurrentItem(0);
        clickTab(0);
    }

    @OnClick(R.id.activity_2)
    public void on2Click() {
        mViewPager.setCurrentItem(1);
        clickTab(1);
    }

    @OnClick(R.id.activity_3)
    public void on3Click() {
        mViewPager.setCurrentItem(2);
        clickTab(2);
    }

    @OnClick(R.id.activity_4)
    public void on4Click() {
        mViewPager.setCurrentItem(3);
        clickTab(3);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activity, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("ActivityFeed");
        setTitle("ACTIVITY");


    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new ActivitiesViewedEvent());
        final LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.activity_container);
        if (mViewPager != null) {
            ll.removeView(mViewPager);
        }
        mViewPager = new ViewPager(getActivity());
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
        FragmentPagerAdapter adapter = new ActivityPagerAdapter(getFragmentManager());
        //mViewPager.setAdapter(adapter);
    }

    private void clickTab(int position) {
        int white = getResources().getColor(R.color.white);
        int green = getResources().getColor(R.color.knodaLighterGreen);
        if (position == 0) {
            ((TextView) view.findViewById(R.id.activity_1)).setTextColor(white);
            ((TextView) view.findViewById(R.id.activity_2)).setTextColor(green);
            ((TextView) view.findViewById(R.id.activity_3)).setTextColor(green);
            ((TextView) view.findViewById(R.id.activity_4)).setTextColor(green);
        } else if (position == 1) {
            ((TextView) view.findViewById(R.id.activity_1)).setTextColor(green);
            ((TextView) view.findViewById(R.id.activity_2)).setTextColor(white);
            ((TextView) view.findViewById(R.id.activity_3)).setTextColor(green);
            ((TextView) view.findViewById(R.id.activity_4)).setTextColor(green);
        } else if (position == 2) {
            ((TextView) view.findViewById(R.id.activity_1)).setTextColor(green);
            ((TextView) view.findViewById(R.id.activity_2)).setTextColor(green);
            ((TextView) view.findViewById(R.id.activity_3)).setTextColor(white);
            ((TextView) view.findViewById(R.id.activity_4)).setTextColor(green);
        } else if (position == 3) {
            ((TextView) view.findViewById(R.id.activity_1)).setTextColor(green);
            ((TextView) view.findViewById(R.id.activity_2)).setTextColor(green);
            ((TextView) view.findViewById(R.id.activity_3)).setTextColor(green);
            ((TextView) view.findViewById(R.id.activity_4)).setTextColor(white);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //adapter.reset();
    }


}

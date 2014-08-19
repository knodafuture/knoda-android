package views.contacts;

/**
 * Created by jeffcailteux on 8/14/14.
 */

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.Random;

import butterknife.InjectView;
import butterknife.OnClick;
import helpers.TypefaceSpan;
import models.UserContact;
import pubsub.LoginFlowDoneEvent;
import unsorted.PagerSlidingTabStrip;
import views.core.BaseFragment;
import views.core.MainActivity;

public class FindFriendsFragment extends BaseFragment {

    public ArrayList<UserContact> following;
    public ArrayList<UserContact> inviting;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.findfriends_title)
    TextView title;
    @InjectView(R.id.findfriends_container)
    LinearLayout container;
    ViewPager mViewPager;
    @InjectView(R.id.findfriends_submit)
    Button submitBtn;
    public FindFriendsFragment() {
    }

    public static FindFriendsFragment newInstance() {
        FindFriendsFragment fragment = new FindFriendsFragment();
        return fragment;
    }

    @OnClick(R.id.wall_close)
    public void close() {
        //dismissFade();
        bus.post(new LoginFlowDoneEvent());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).hideNavbar();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_findfriends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SpannableString s = new SpannableString("FIND FRIENDS");
        s.setSpan(new TypefaceSpan(getActivity(), "KronaOne-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(s);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getActivity().getResources().getDisplayMetrics());
        final int onesp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getActivity().getResources().getDisplayMetrics());
        Point size = new Point();
        display.getSize(size);
        tabs.setIndicatorHeight(onedp * 4);
        tabs.setTextSize(onesp * 16);
        tabs.setBackgroundColor(0xE0E0E0);
        tabs.setTabWidth((int) (size.x * 1.0f / 3));


        if (mViewPager != null) {
            container.removeView(mViewPager);
        }
        mViewPager = new ViewPager(getActivity());
        mViewPager.setId(2000 + new Random().nextInt(1000));
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
        container.addView(mViewPager);
        // FindFriendsPagerAdapter adapter = new FindFriendsPagerAdapter(getFragmentManager(), this);
        //mViewPager.setAdapter(adapter);
        tabs.setViewPager(mViewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

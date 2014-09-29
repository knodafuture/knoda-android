package views.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.Random;

import adapters.ProfilePagerAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import helpers.TypefaceSpan;
import models.ServerError;
import models.SettingsCategory;
import models.User;
import networking.NetworkListCallback;
import pubsub.ProfilePagerScrollEvent;
import views.avatar.UserAvatarChooserActivity;
import views.core.BaseFragment;
import views.core.MainActivity;

public class MyProfileFragment extends BaseFragment implements MyProfileActionBar.MyProfileActionBarCallbacks {

    public boolean loaded = false;
    public LinearLayout.LayoutParams params;
    @InjectView(R.id.topview)
    LinearLayout topview;
    @InjectView(R.id.profile_avatar)
    NetworkImageView avatarIcon;
    @InjectView(R.id.profile_points)
    TextView tv_points;
    @InjectView(R.id.profile_winpercent)
    TextView tv_winpercent;
    @InjectView(R.id.profile_winstreak)
    TextView tv_winstreak;
    @InjectView(R.id.profile_winloss)
    TextView tv_winloss;
    @InjectView(R.id.profile_followers)
    TextView tv_followers;
    @InjectView(R.id.profile_following)
    TextView tv_following;
    TextView selectedFilter;
    View selectedUnderline;
    ProfilePagerAdapter adapter;
    MainActivity mainActivity;
    @InjectView(R.id.topContainer)
    LinearLayout topContainer;
    int topContainerHeight;
    MyProfileActionBar actionbar;
    private ViewPager mViewPager;

    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    @OnClick(R.id.activity_1)
    void onClickPredictions() {
        mViewPager.setCurrentItem(0, true);
    }

    @OnClick(R.id.activity_2)
    void onClickVotes() {
        mViewPager.setCurrentItem(1, true);
    }

    @OnClick(R.id.profile_avatar)
    void OnClickAvatar() {
        if (userManager.getUser().guestMode) {
            ((MainActivity) getActivity()).showLogin("Whoa there!", "You need to be a registered user to change your avatar!");
        } else {
            Intent intent = new Intent(getActivity(), UserAvatarChooserActivity.class);
            intent.putExtra("cancelable", true);
            startActivityForResult(intent, 123123129);
        }
    }

    @OnClick(R.id.profile_following_container)
    void onClickFollowing() {
        FollowFragment followFragment = FollowFragment.newInstance(1, userManager.getUser());
        pushFragment(followFragment);
    }

    @OnClick(R.id.profile_followers_container)
    void onClickFollowers() {
        FollowFragment followFragment = FollowFragment.newInstance(0, userManager.getUser());
        pushFragment(followFragment);
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        networkingManager.getSettings(new NetworkListCallback<SettingsCategory>() {
            @Override
            public void completionHandler(ArrayList<SettingsCategory> object, ServerError error) {
                if (error == null) {
                    for (SettingsCategory s : object) {
                        if (getActivity() instanceof MainActivity)
                            ((MainActivity) getActivity()).settings.put(s.name, s.settings);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myprofile, container, false);
        ButterKnife.inject(this, view);
        getActivity().invalidateOptionsMenu();

        selectedFilter = (TextView) view.findViewById(R.id.activity_1);
        selectedUnderline = view.findViewById(R.id.underline_1);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("Profile_Screen");
    }

    @Override
    public void onResume() {
        super.onResume();
        final User user = userManager.getUser();
        ((MainActivity) getActivity()).resetNavIcons();
        getActivity().findViewById(R.id.nav_profile).setBackgroundResource(R.drawable.nav_me_active);
        ((TextView) getActivity().findViewById(R.id.nav_profile_text)).setTextColor(Color.parseColor("#EFEFEF"));
        updateUser(user);
        topContainer = (LinearLayout) topview.findViewById(R.id.topContainer);

        if (userManager.getUser() == null || userManager.getUser().guestMode)
            changeFilter(R.id.activity_2);
        else
            changeFilter(R.id.activity_1);

        topContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                topContainerHeight = topContainer.getHeight();
                params = (LinearLayout.LayoutParams) topContainer.getLayoutParams();
                topContainer.removeOnLayoutChangeListener(this);
                loaded = true;
            }
        });
        if (mViewPager != null) {
            topview.removeView(mViewPager);
        }
        mViewPager = new ViewPager(getActivity().getApplicationContext());
        mViewPager.setId(2000 + new Random().nextInt(100));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                bus.post(new ProfilePagerScrollEvent());
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
        adapter = new ProfilePagerAdapter(getFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        if (userManager.getUser() == null || userManager.getUser().guestMode)
            mViewPager.setCurrentItem(1);
        else
            mViewPager.setCurrentItem(0);

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.profile, menu);
        MenuItem menuItem = menu.findItem(R.id.myprofile_actionbar);
        actionbar = (MyProfileActionBar) menuItem.getActionView();
        actionbar.setMode(userManager.getUser());
        getActivity().getActionBar().setDisplayShowHomeEnabled(false);
        getActivity().getActionBar().setDisplayUseLogoEnabled(false);
        getActivity().getActionBar().setDisplayShowTitleEnabled(false);
        actionbar.setCallbacks(this);


        SpannableString s = new SpannableString(userManager.getUser().username.toUpperCase());
        s.setSpan(new TypefaceSpan(getActivity(), "KronaOne-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionbar.titleTV.setText(s);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayShowHomeEnabled(true);
        getActivity().getActionBar().setDisplayUseLogoEnabled(true);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
    }

    private void updateUser(User user) {
        if (user == null)
            return;

        setTitle(user.username.toUpperCase());

        if (user.avatar != null)
            avatarIcon.setImageUrl(user.avatar.big, networkingManager.getImageLoader());

        tv_points.setText(user.points.toString());
        tv_winstreak.setText(setStreak(user.streak));
        tv_winpercent.setText(user.winningPercentage.toString() + "%");
        tv_winloss.setText(user.won.toString() + "-" + user.lost.toString());
        tv_followers.setText(user.follower_count + "");
        tv_following.setText(user.following_count + "");
    }

    private String setStreak(String s) {
        if (s == null || s.equals(""))
            return "W0";
        else
            return s;

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
    public void onSettingsClick() {
        ((MainActivity) getActivity()).onSettings();
    }

    @Override
    public void onVersusClick() {
        pushFragment(new HeadToHeadFragment());
    }

    @Override
    public void onSignUpClick() {
        mainActivity.showLogin("Giddy Up!", "Now we're talking! Choose an option below to sign-up and start tracking your predictions.");
    }

}

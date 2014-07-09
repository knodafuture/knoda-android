package views.profile;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import models.ServerError;
import models.SettingsCategory;
import models.User;
import networking.NetworkListCallback;
import views.core.BaseFragment;
import views.core.MainActivity;

public class MyProfile2Fragment extends BaseFragment {

    @InjectView(R.id.topview)
    LinearLayout topview;
    @InjectView(R.id.profile_twitter)
    ImageView twitterIcon;
    @InjectView(R.id.profile_facebook)
    ImageView facebookIcon;
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

    @OnClick(R.id.activity_1)
    void onClickPredictions() {
        mViewPager.setCurrentItem(0, true);
    }

    @OnClick(R.id.activity_2)
    void onClickVotes() {
        mViewPager.setCurrentItem(1, true);
    }

    private ViewPager mViewPager;
    TextView selectedFilter;
    View selectedUnderline;

    public static MyProfile2Fragment newInstance() {
        MyProfile2Fragment fragment = new MyProfile2Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).networkingManager.getSettings(new NetworkListCallback<SettingsCategory>() {
            @Override
            public void completionHandler(ArrayList<SettingsCategory> object, ServerError error) {
                for (SettingsCategory s : object) {
                    ((MainActivity) getActivity()).settings.put(s.name, s.settings);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myprofile2, container, false);
        ButterKnife.inject(this, view);
        getActivity().invalidateOptionsMenu();

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

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
        updateUser(user);

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
        inflater.inflate(R.menu.profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_search);
        menu.removeItem(R.id.action_add_prediction);
        if (((MainActivity) getActivity()).currentFragment.equals(this.getClass().getSimpleName()) && menu.findItem(R.id.action_settings) != null)
            menu.findItem(R.id.action_settings).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }


    private void updateUser(User user) {
        if (user == null)
            return;

        setTitle(user.username.toUpperCase());

        if (user.avatar != null)
            avatarIcon.setImageUrl(user.avatar.big, networkingManager.getImageLoader());

        if (user.getFacebookAccount() != null) {
            facebookIcon.setImageResource(R.drawable.profile_facebook_active);
        } else {
            facebookIcon.setImageResource(R.drawable.profile_facebook);
        }
        if (user.getTwitterAccount() != null) {
            twitterIcon.setImageResource(R.drawable.profile_twitter_active);
        } else {
            twitterIcon.setImageResource(R.drawable.profile_twitter);
        }

        tv_points.setText(user.points.toString());
        tv_winstreak.setText(user.streak.toString());
        tv_winpercent.setText(user.winningPercentage.toString() + "%");
        tv_winloss.setText(user.won.toString() + "-" + user.lost.toString());
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
        //sharedPrefManager.setSavedActivityFilter(id);
        selectedFilter.setTextColor(getResources().getColor(R.color.knodaLighterGreen));
        selectedFilter = ((TextView) topview.findViewById(id));
        selectedFilter.setTextColor(Color.WHITE);
        selectedUnderline.setVisibility(View.VISIBLE);
    }


}

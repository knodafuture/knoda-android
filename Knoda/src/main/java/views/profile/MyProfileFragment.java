package views.profile;

import android.app.AlertDialog;
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

import adapters.ProfilePagerAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import managers.FacebookManager;
import managers.TwitterManager;
import models.ServerError;
import models.SettingsCategory;
import models.SocialAccount;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.ProfilePagerScrollEvent;
import unsorted.ErrorReporter;
import unsorted.Logger;
import views.avatar.UserAvatarChooserActivity;
import views.core.BaseFragment;
import views.core.MainActivity;
import views.core.Spinner;

public class MyProfileFragment extends BaseFragment {

    public FacebookManager facebookManager;
    public TwitterManager twitterManager;
    public ErrorReporter errorReporter;
    public Spinner spinner;
    public boolean loaded = false;
    public LinearLayout.LayoutParams params;
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
    TextView selectedFilter;
    View selectedUnderline;
    ProfilePagerAdapter adapter;
    MainActivity mainActivity;
    @InjectView(R.id.topContainer)
    LinearLayout topContainer;
    int topContainerHeight;
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

    @OnClick(R.id.profile_facebook)
    void onClickFacebook() {
        handleFB();
    }

    @OnClick(R.id.profile_twitter)
    void onClickTwitter() {
        handleTwitter();
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

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mainActivity = ((MainActivity) getActivity());
        setHasOptionsMenu(true);
        mainActivity.networkingManager.getSettings(new NetworkListCallback<SettingsCategory>() {
            @Override
            public void completionHandler(ArrayList<SettingsCategory> object, ServerError error) {
                if (error == null) {
                    for (SettingsCategory s : object) {
                        ((MainActivity) getActivity()).settings.put(s.name, s.settings);
                    }
                }
            }
        });
        spinner = mainActivity.spinner;
        facebookManager = new FacebookManager(userManager, networkingManager);
        twitterManager = mainActivity.twitterManager;
        errorReporter = mainActivity.errorReporter;

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
        if (sharedPrefManager.getTwitterAuthScreen().equals("profile")) {
            sharedPrefManager.setTwitterAuthScreen("");
            //twitter update
            if (twitterManager.hasAuthInfo())
                finishAddingTwitterAccount();
            else
                errorReporter.showError("Error authorizing with Twitter. Please try again later.");
        }
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
        if (mainActivity.userManager.getUser() != null && mainActivity.userManager.getUser().guestMode == false)
            inflater.inflate(R.menu.profile, menu);
        else
            inflater.inflate(R.menu.profile_guest, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mainActivity.userManager.getUser() != null && mainActivity.userManager.getUser().guestMode == false) {
            menu.removeItem(R.id.action_search);
            if (((MainActivity) getActivity()).currentFragment.equals(this.getClass().getSimpleName()) && menu.findItem(R.id.action_settings) != null)
                menu.findItem(R.id.action_settings).setVisible(true);
        } else {
            if (((MainActivity) getActivity()).currentFragment.equals(this.getClass().getSimpleName()) && menu.findItem(R.id.action_settings) != null)
                menu.findItem(R.id.action_settings).setVisible(false);
        }
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
        tv_winstreak.setText(setStreak(user.streak));
        tv_winpercent.setText(user.winningPercentage.toString() + "%");
        tv_winloss.setText(user.won.toString() + "-" + user.lost.toString());
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

    private void handleFB() {
        if (userManager.getUser().getFacebookAccount() != null)
            removeFBAccount();
        else
            addFBAccount();
    }

    private void addFBAccount() {
        spinner.show();
        facebookManager.openSession(getActivity(), new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    spinner.hide();
                    errorReporter.showError(error);
                    return;
                }

                userManager.addSocialAccount(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User user, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }
                        updateUser(user);
                    }
                });
            }
        });
    }

    private void removeFBAccount() {
        if (userManager.getUser().email == null && userManager.getUser().getTwitterAccount() == null) {
            errorReporter.showError("You must enter an email address before removing your last social account, or your account will be lost forever.");
            return;
        }

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setTitle("Are you sure you wish to remove your Facebook account?")
                .create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                spinner.show();
                userManager.deleteSocialAccount(userManager.getUser().getFacebookAccount(), new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }
                        updateUser(object);
                    }
                });
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }


    private void handleTwitter() {
        if (userManager.getUser() != null && userManager.getUser().getTwitterAccount() != null)
            removeTwitterAccount();
        else
            addTwitterAccount();
    }

    private void addTwitterAccount() {
        if (twitterManager.hasAuthInfo()) {
            finishAddingTwitterAccount();
        }
        spinner.show();
        sharedPrefManager.setTwitterAuthScreen("profile");
        twitterManager.openSession(getActivity());
    }

    private void finishAddingTwitterAccount() {
        Logger.log("FINISHING TWITTER -------");
        spinner.show();
        twitterManager.getSocialAccount(new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    errorReporter.showError(error);
                    spinner.hide();
                    return;
                }

                userManager.addSocialAccount(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User user, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }
                        updateUser(user);
                    }
                });
            }
        });
    }

    private void removeTwitterAccount() {
        if (userManager.getUser().email == null && userManager.getUser().getFacebookAccount() == null) {
            errorReporter.showError("You must enter an email address before removing your last social account, or your account will be lost forever.");
            return;
        }

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setTitle("Are you sure you wish to remove your Twitter account?")
                .create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                spinner.show();
                userManager.deleteSocialAccount(userManager.getUser().getTwitterAccount(), new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }
                        twitterManager.clearTwitterInfo();
                        updateUser(object);
                    }
                });
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

}

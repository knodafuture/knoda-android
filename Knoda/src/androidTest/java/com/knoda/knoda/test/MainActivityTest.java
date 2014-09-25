package com.knoda.knoda.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.util.ArrayList;

import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.UserManager;
import models.Follow;
import models.Notification;
import models.User;
import views.contests.SocialFragment;
import views.core.MainActivity;

public class MainActivityTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private Solo solo;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testPreconditions() {
        assertNotNull("mainActivity is null", mainActivity);
        solo.assertCurrentActivity("main is current", MainActivity.class);
    }

    public void testFollows() {
        ArrayList<Follow> myfollowing = new ArrayList<Follow>();
        Follow follow = new Follow();
        follow.leader_id = 2;
        myfollowing.add(follow);
        assertNotNull(mainActivity.helper.checkIfFollowingUser(2, myfollowing));
    }

    public void testPushNotifications() {
        Notification pushNotifcation = new Notification();
        Intent testIntent = new Intent();
        testIntent.putExtra("type", "test");
        testIntent.putExtra("id", 2);
        testIntent.putExtra("message", "testing");
        mainActivity.helper.handlePushNotification(testIntent, pushNotifcation);
        assertTrue(mainActivity.spinner.isVisible());
    }

    public void testCheckFragmentGuest() {
        SocialFragment fragment = SocialFragment.newInstance();
        assertTrue(!mainActivity.helper.checkFragment(fragment));
    }

    public void testCheckFragmentLoggedIn() {
        SocialFragment fragment = SocialFragment.newInstance();
        mainActivity.networkingManager = new NetworkingManager(mainActivity);
        mainActivity.sharedPrefManager = new SharedPrefManager(mainActivity);
        mainActivity.userManager = new UserManager(mainActivity.networkingManager, mainActivity.sharedPrefManager);
        mainActivity.userManager.user = new User();
        mainActivity.userManager.user.guestMode = false;
        assertTrue(mainActivity.helper.checkFragment(fragment));
    }
}
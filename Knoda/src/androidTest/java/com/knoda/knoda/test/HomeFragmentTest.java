package com.knoda.knoda.test;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import models.User;
import views.core.MainActivity;
import views.predictionlists.HomeFragment;

public class HomeFragmentTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private Solo solo;
    private HomeFragment homeFragment;

    public HomeFragmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        solo = new Solo(getInstrumentation(), getActivity());
        homeFragment = mainActivity.homeFragment;
        //mainActivity.networkingManager = new NetworkingManager(mainActivity);
        //mainActivity.sharedPrefManager = new SharedPrefManager(mainActivity);
        //mainActivity.userManager = new UserManager(mainActivity.networkingManager, mainActivity.sharedPrefManager);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testPreconditions() {
        assertNotNull(mainActivity.homeFragment);
    }

    public void testHomeActionBar() throws InterruptedException {
        solo.sleep(3);
        solo.clickOnButton(0);
        solo.waitForDialogToClose();
        solo.clickOnText("FOLLOWING");
        solo.sleep(1);
        assertEquals(homeFragment.homeActionBar.selected, 1);
    }

    public void testWalkthrough() {
        homeFragment.accessAdapter().currentPage = 0;
        solo.sleep(3);
        solo.clickOnButton(0);
        solo.waitForDialogToClose();
        mainActivity.sharedPrefManager.setFirstLaunch(false);
        mainActivity.sharedPrefManager.setHaveShownPredictionWalkthrough(false);
        User user = new User();
        user.guestMode = false;
        user.points = 1;
        homeFragment.helper.checkWalkthrough(user);
        solo.sleep(2);
        assertTrue(homeFragment.sharedPrefManager.haveShownPredictionWalkthrough());
    }


}
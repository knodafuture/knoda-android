package com.knoda.knoda.test;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import views.core.MainActivity;

public class HomeFragmentTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private Solo solo;

    public HomeFragmentTest() {
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

    public void testHomeActionBar() throws InterruptedException {
        assertNotNull(mainActivity.homeFragment);
        solo.sleep(3);
        solo.clickOnButton(0);
        solo.waitForDialogToClose();
        solo.clickOnText("FOLLOWING");
        solo.sleep(1);
        assertEquals(mainActivity.homeFragment.homeActionBar.selected, 1);
    }

}
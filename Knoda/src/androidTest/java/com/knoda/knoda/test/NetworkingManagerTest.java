package com.knoda.knoda.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import java.util.ArrayList;

import builders.ParamBuilder;
import managers.NetworkingManager;
import models.Prediction;
import models.ServerError;
import networking.NetworkListCallback;
import views.core.MainActivity;
import views.predictionlists.HomeFragment;

/**
 * Created by jeffcailteux on 9/26/14.
 */
public class NetworkingManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private Solo solo;
    private NetworkingManager networkingManager;
    private HomeFragment homeFragment;


    public NetworkingManagerTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        mainActivity.networkingManager = new NetworkingManager(mainActivity);
        networkingManager = mainActivity.networkingManager;
        solo = new Solo(getInstrumentation(), getActivity());
        homeFragment = (HomeFragment) mainActivity.getFragmentManager().findFragmentByTag("HomeFragment");
    }

    @Override
    public void tearDown() throws Exception {
        networkingManager.stopCalls();
        solo.finishOpenedActivities();
    }

    @UiThreadTest
    public void testGetPredictions() {
        Condition condition = new Condition() {
            @Override
            public boolean isSatisfied() {
                return homeFragment.accessAdapter().getCount() > 0;
            }
        };
        solo.waitForCondition(condition, 5);
        networkingManager.getPredictionsNoUser(new NetworkListCallback<Prediction>() {
            @Override
            public void completionHandler(ArrayList<Prediction> object, ServerError error) {
                if (error == null)
                    assertTrue(true);
                else
                    assertTrue(false);
            }
        });
    }

    @UiThreadTest
    public void testPreconditions() {
        assertNotNull(mainActivity.networkingManager);
        assertNotNull(mainActivity.networkingManager.getImageLoader());
    }

    public void testBuildURL() {
        ParamBuilder builder = ParamBuilder.create().add("recent", "true");
        assertEquals(networkingManager.returnBuildURL("predictions.json", builder), "http://captaincold.knoda.com/api/predictions.json?recent=true");
    }


}

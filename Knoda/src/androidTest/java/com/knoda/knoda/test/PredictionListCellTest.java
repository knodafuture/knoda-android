package com.knoda.knoda.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.knoda.knoda.R;
import com.robotium.solo.Solo;

import managers.SharedPrefManager;
import models.Challenge;
import models.Prediction;
import views.core.MainActivity;
import views.predictionlists.PredictionListCell;

public class PredictionListCellTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private Solo solo;
    private PredictionListCell listCell;

    public PredictionListCellTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        mainActivity.sharedPrefManager = new SharedPrefManager(mainActivity);
        solo = new Solo(getInstrumentation(), getActivity());
        listCell = new PredictionListCell(mainActivity);
    }

    @Override
    public void tearDown() throws Exception {
        mainActivity.networkingManager.stopCalls();
        solo.finishOpenedActivities();
    }

    public void testPreconditions() {
        assertNotNull(listCell);
    }

    public void testSetPrediction() {
        Prediction prediction = new Prediction();
        prediction.body = "test_body";
        prediction.username = "test_username";
        prediction.expiredText = "test_expired";
        prediction.predictedText = "test_predicted";
        prediction.agreedCount = 2;
        prediction.disagreedCount = 2;
        prediction.commentCount = 0;
        prediction.verifiedAccount = false;
        Challenge challenge = new Challenge();
        challenge.agree = true;
        challenge.isOwn = false;
        prediction.userId = 1;
        prediction.challenge = challenge;
        prediction.isReadyForResolution = false;
        prediction.settled = false;

        listCell.setPrediction(prediction, mainActivity, false);

        assertEquals(listCell.bodyTextView.getText().toString(), "test_body");
        assertEquals(listCell.usernameTextView.getText().toString(), "test_username");
        assertEquals(listCell.timeStampsTextView.getText().toString(), "test_expired | test_predicted | 50% agree | ");
        assertEquals(listCell.commentCountTextView.getText().toString(), "0");
        assertEquals(listCell.groupView.getVisibility(), View.GONE);
        assertEquals(listCell.avatarImageView.getTag(), 1);
        assertEquals(listCell.textContainer.getTag(), 1);
        assertEquals(listCell.resultTextView.getText().toString(), "");
    }

    public void testVoteImage() {
        assertEquals(listCell.testVoteImage(new Prediction()), 0);

        Prediction prediction = new Prediction();
        Challenge challenge = new Challenge();
        challenge.agree = true;
        challenge.isOwn = false;
        prediction.challenge = challenge;
        assertEquals(listCell.testVoteImage(prediction), R.drawable.agree_marker);

        prediction.challenge.agree = false;
        assertEquals(listCell.testVoteImage(prediction), R.drawable.disagree_marker);
    }


}
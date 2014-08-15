package managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.knoda.knoda.R;

import javax.inject.Singleton;

import factories.GsonF;
import models.LoginRequest;
import models.LoginResponse;
import models.Prediction;
import models.SignUpRequest;
import models.SocialAccount;

/**
 * Created by nick on 1/21/14.
 */

@Singleton
public class SharedPrefManager {

    private static final String SAVED_USERNAME_KEY = "SAVEDUSERNAME";
    private static final String SAVED_PASSWORD_KEY = "SAVEDPASSWORD";
    private static final String SAVED_AUTHTOKEN_KEY = "SAVEDAUTHTOKEN";
    private static final String REG_ID_KEY = "REGISTRATION_ID";
    private static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH";
    private static final String SAVED_SOCIAL_ACCOUNT_KEY = "SOCIAL_ACCOUNT_SAVED";
    private static final String SAVED_PREDICTION_IN_PROGESS_KEY = "PREDICTION_IN_PROGRESS";
    private static final String SAVED_GUEST_MODE_KEY = "GUEST_MODE_KEY";
    private static final String SAVED_PREDICTION_WALKTHROUGH_KEY = "SAVED_PREDICTION_WALKTHROUGH";
    private static final String SAVED_VOTING_WALKTHROUGH_KEY = "SAVED_VOTING_WALKTHROUGHT";
    private static final String SAVED_CONTEST_VOTING_WALKTHROUGH_KEY = "SAVED_CONTEST_VOTING_WALKTHROUGHT";
    private static final String SAVED_AGREED_TO_TERMS_KEYS = "SAVED_AGREEED_TO_TERMS_KEY";
    private static final String SAVED_ACTIVITY_FILTER = "SAVED_ACTIVITY_FILTER";
    private static final String TWITTER_AUTH_SCREEN = "TWITTER_AUTH_SCREEN";
    private static final String SAVED_PREDICT_DATE_WALKTHROUGH_KEY = "SAVED_PREDICT_DATE_WALKTHROUGH_KEY";
    private static final String API_URL = "API_URL";
    private Context context;

    public SharedPrefManager(Context context) {
        this.context = context;
    }

    private SharedPreferences getSP() {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

    }

    public void saveLoginRequestAndResponse(LoginRequest request, LoginResponse response) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_USERNAME_KEY, response.email).commit();
        sharedPreferences.edit().putString(SAVED_PASSWORD_KEY, request.password).commit();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, response.authToken).commit();
        sharedPreferences.edit().putBoolean(SAVED_GUEST_MODE_KEY, false).commit();
    }

    public void saveSignupRequestAndResponse(SignUpRequest request, LoginResponse response) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_USERNAME_KEY, request.email).commit();
        sharedPreferences.edit().putString(SAVED_PASSWORD_KEY, request.password).commit();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, response.authToken).commit();
        sharedPreferences.edit().putBoolean(SAVED_GUEST_MODE_KEY, false).commit();
    }

    public void saveSignupRequest(SignUpRequest request) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_USERNAME_KEY, request.email).commit();
        sharedPreferences.edit().putString(SAVED_PASSWORD_KEY, request.password).commit();
        sharedPreferences.edit().putBoolean(SAVED_GUEST_MODE_KEY, false).commit();
    }

    public void saveGuestCredentials(LoginResponse response) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putBoolean(SAVED_GUEST_MODE_KEY, true).commit();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, response.authToken).commit();
    }

    public void saveSocialAccountAndResponse(SocialAccount account, LoginResponse response) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, response.authToken).commit();
        sharedPreferences.edit().putBoolean(SAVED_GUEST_MODE_KEY, false).commit();
        sharedPreferences.edit().putString(SAVED_SOCIAL_ACCOUNT_KEY, GsonF.actory().toJson(account)).commit();
    }

    public boolean guestMode() {
        SharedPreferences sharedPreferences = getSP();

        return sharedPreferences.getBoolean(SAVED_GUEST_MODE_KEY, false);
    }

    public void saveGcm(String regId) {
        final SharedPreferences prefs = getSP();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID_KEY, regId);
        editor.commit();
    }

    public String getGcmRegId() {
        return getSP().getString(REG_ID_KEY, "");
    }

//    public LoginRequest getSavedLoginRequest() {
//
//        SharedPreferences sharedPreferences = getSP();
//        String login = sharedPreferences.getString(SAVED_USERNAME_KEY, null);
//        String password = sharedPreferences.getString(SAVED_PASSWORD_KEY, null);
//
//        if (login == null || password == null)
//            return null;
//
//        return new LoginRequest(login, password);
//    }

//    public SocialAccount getSavedSocialAccount() {
//        SharedPreferences sharedPreferences = getSP();
//        String json = sharedPreferences.getString(SAVED_SOCIAL_ACCOUNT_KEY, null);
//
//        if (json == null)
//            return null;
//
//        SocialAccount account = GsonF.actory().fromJson(json, SocialAccount.class);
//
//        return account;
//    }

    public String getSavedAuthtoken() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getString(SAVED_AUTHTOKEN_KEY, null);
    }

    public void setSavedAuthtoken(String authtoken) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, authtoken).commit();
    }

    public void clearSession() {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences
                .edit()
                .remove(SAVED_PASSWORD_KEY)
                .remove(SAVED_USERNAME_KEY)
                .remove(SAVED_AUTHTOKEN_KEY)
                .remove(SAVED_SOCIAL_ACCOUNT_KEY)
                .remove(SAVED_GUEST_MODE_KEY)
                .remove("0activity")
                .remove("1activity")
                .remove("2activity")
                .remove("3activity")
                .remove("enteredcontests")
                .remove("explorecontests")
                .remove("0profile")
                .remove("1profile")
                .commit();
    }

    public void clearUnsafeData() {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences
                .edit()
                .remove(SAVED_PASSWORD_KEY)
                .remove(SAVED_USERNAME_KEY)
                .commit();
    }

    public boolean getFirstLaunch() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getBoolean(FIRST_LAUNCH_KEY, true);

    }

    public void setFirstLaunch(boolean firstLaunch) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putBoolean(FIRST_LAUNCH_KEY, firstLaunch).commit();
    }

    public void clearPredictionInProgress() {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().remove(SAVED_PREDICTION_IN_PROGESS_KEY).commit();
    }

    public Prediction getPredictionInProgress() {
        SharedPreferences sharedPreferences = getSP();
        String predictionJson = sharedPreferences.getString(SAVED_PREDICTION_IN_PROGESS_KEY, null);

        if (predictionJson == null)
            return null;

        return GsonF.actory().fromJson(predictionJson, Prediction.class);
    }

    public void setPredictionInProgress(Prediction prediction) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_PREDICTION_IN_PROGESS_KEY, GsonF.actory().toJson(prediction)).commit();
    }

    public boolean haveShownPredictionWalkthrough() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getBoolean(SAVED_PREDICTION_WALKTHROUGH_KEY, false);
    }

    public void setHaveShownPredictionWalkthrough(boolean haveShownPredictionWalkthrough) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putBoolean(SAVED_PREDICTION_WALKTHROUGH_KEY, haveShownPredictionWalkthrough).commit();
    }

    public boolean shouldShowVotingWalkthrough() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getBoolean(SAVED_VOTING_WALKTHROUGH_KEY, false);
    }

    public void setShouldShowVotingWalkthrough(boolean shouldShowVotingWalkthrough) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putBoolean(SAVED_VOTING_WALKTHROUGH_KEY, shouldShowVotingWalkthrough).commit();
    }

    public boolean shouldShowContestVotingWalkthrough() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getBoolean(SAVED_CONTEST_VOTING_WALKTHROUGH_KEY, true);
    }

    public void setShouldShowContestVotingWalkthrough(boolean shouldShowContestVotingWalkthrough) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putBoolean(SAVED_CONTEST_VOTING_WALKTHROUGH_KEY, shouldShowContestVotingWalkthrough).commit();
    }

    public boolean agreedToTerms() {
        return getSP().getBoolean(SAVED_AGREED_TO_TERMS_KEYS, false);
    }

    public void setAgreedToTerms(boolean agreedToTerms) {
        getSP().edit().putBoolean(SAVED_AGREED_TO_TERMS_KEYS, agreedToTerms).commit();
    }

    public int getSavedActivityFilter() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getInt(SAVED_ACTIVITY_FILTER, R.id.activity_1);

    }

    public void setSavedActivityFilter(int filter) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putInt(SAVED_ACTIVITY_FILTER, filter).commit();
    }

    public String getTwitterAuthScreen() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getString(TWITTER_AUTH_SCREEN, "");
    }

    public void setTwitterAuthScreen(String screen) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(TWITTER_AUTH_SCREEN, screen).commit();
    }


    public boolean shouldShowPredictDateWalkthrough() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getBoolean(SAVED_PREDICT_DATE_WALKTHROUGH_KEY, true);
    }

    public void sethouldShowPredictDateWalkthrough(boolean shouldShowVotingWalkthrough) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putBoolean(SAVED_PREDICT_DATE_WALKTHROUGH_KEY, shouldShowVotingWalkthrough).commit();
    }

    public String getAPIurl() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getString(API_URL, NetworkingManager.baseUrl);
    }

    public void setAPIurl(String url) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(API_URL, url).commit();
    }

    public void saveObjectString(Object object, String name) {
        SharedPreferences sharedPreferences = getSP();
        Gson gson = GsonF.actory();
        sharedPreferences.edit().putString(name, gson.toJson(object)).commit();
    }

    public String getObjectString(String name) {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getString(name, null);
    }

}


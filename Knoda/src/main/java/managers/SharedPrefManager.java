package managers;

import android.content.Context;
import android.content.SharedPreferences;

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

    private Context context;

    private static final String SAVED_USERNAME_KEY = "SAVEDUSERNAME";
    private static final String SAVED_PASSWORD_KEY = "SAVEDPASSWORD";
    private static final String SAVED_AUTHTOKEN_KEY = "SAVEDAUTHTOKEN";
    private static final String REG_ID_KEY = "REGISTRATION_ID";
    private static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH";
    private static final String SAVED_SOCIAL_ACCOUNT_KEY = "SOCIAL_ACCOUNT_SAVED";
    private static final String SAVED_PREDICTION_IN_PROGESS_KEY = "PREDICTION_IN_PROGRESS";

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
    }

    public void saveSignupRequestAndResponse(SignUpRequest request, LoginResponse response) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_USERNAME_KEY, request.email).commit();
        sharedPreferences.edit().putString(SAVED_PASSWORD_KEY, request.password).commit();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, response.authToken).commit();
    }

    public void saveSocialAccountAndResponse(SocialAccount account, LoginResponse response) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, response.authToken).commit();
        sharedPreferences.edit().putString(SAVED_SOCIAL_ACCOUNT_KEY, GsonF.actory().toJson(account)).commit();
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

    public LoginRequest getSavedLoginRequest() {

        SharedPreferences sharedPreferences = getSP();
        String login = sharedPreferences.getString(SAVED_USERNAME_KEY, null);
        String password = sharedPreferences.getString(SAVED_PASSWORD_KEY, null);

        if (login == null || password == null)
            return null;

        return new LoginRequest(login, password);
    }

    public SocialAccount getSavedSocialAccount() {
        SharedPreferences sharedPreferences = getSP();
        String json = sharedPreferences.getString(SAVED_SOCIAL_ACCOUNT_KEY, null);

        if (json == null)
            return null;

        SocialAccount account = GsonF.actory().fromJson(json, SocialAccount.class);

        return account;
    }

    public void setSavedAuthtoken(String authtoken) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, authtoken).commit();
    }

    public String getSavedAuthtoken () {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getString(SAVED_AUTHTOKEN_KEY, null);
    }

    public void clearSession() {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences
            .edit()
            .remove(SAVED_PASSWORD_KEY)
            .remove(SAVED_USERNAME_KEY)
            .remove(SAVED_AUTHTOKEN_KEY)
            .remove(SAVED_SOCIAL_ACCOUNT_KEY)
            .commit();
    }

    public void setFirstLaunch(boolean firstLaunch) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putBoolean(FIRST_LAUNCH_KEY, firstLaunch).commit();
    }

    public boolean getFirstLaunch() {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getBoolean(FIRST_LAUNCH_KEY, true);

    }

    public void setPredictionInProgress(Prediction prediction) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_PREDICTION_IN_PROGESS_KEY, GsonF.actory().toJson(prediction)).commit();
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

}


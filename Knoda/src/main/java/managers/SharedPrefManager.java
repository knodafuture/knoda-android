package managers;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import models.LoginRequest;
import models.LoginResponse;
import models.SignUpRequest;
import views.core.MainActivity;

/**
 * Created by nick on 1/21/14.
 */

@Singleton
public class SharedPrefManager {

    private MainActivity activity;

    private static final String SAVED_USERNAME_KEY = "SAVEDUSERNAME";
    private static final String SAVED_PASSWORD_KEY = "SAVEDPASSWORD";
    private static final String SAVED_AUTHTOKEN_KEY = "SAVEDAUTHTOKEN";

    public SharedPrefManager(MainActivity activity) {
        this.activity = activity;
    }

    private SharedPreferences getSP() {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);

    }
    public void saveLoginRequestAndResponse(LoginRequest request, LoginResponse response) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_USERNAME_KEY, response.email).commit();
        sharedPreferences.edit().putString(SAVED_PASSWORD_KEY, request.password).commit();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, response.authToken).commit();
    }

    public void saveSignupRequestAndResponse(SignUpRequest request, LoginResponse response) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_USERNAME_KEY, response.email).commit();
        sharedPreferences.edit().putString(SAVED_PASSWORD_KEY, request.password).commit();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, response.authToken).commit();
    }

    public LoginRequest getSavedLoginRequest() {

        SharedPreferences sharedPreferences = getSP();
        String login = sharedPreferences.getString(SAVED_USERNAME_KEY, null);
        String password = sharedPreferences.getString(SAVED_PASSWORD_KEY, null);

        if (login == null || password == null)
            return null;

        return new LoginRequest(login, password);
    }

    public void setSavedAuthtoken(String authtoken) {
        SharedPreferences sharedPreferences = getSP();
        sharedPreferences.edit().putString(SAVED_AUTHTOKEN_KEY, authtoken).commit();
    }

    public String getSavedAuthtoken () {
        SharedPreferences sharedPreferences = getSP();
        return sharedPreferences.getString(SAVED_AUTHTOKEN_KEY, null);
    }

}


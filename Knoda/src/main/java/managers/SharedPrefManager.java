package managers;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import models.LoginRequest;
import models.LoginResponse;
import models.SignUpRequest;

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
            .commit();
    }

}


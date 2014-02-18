package managers;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import managers.NetworkingManager;
import managers.SharedPrefManager;
import models.AndroidDeviceToken;
import models.ServerError;
import networking.NetworkCallback;
import unsorted.Logger;

public class GcmManager {

    private NetworkingManager networkingManager;
    private SharedPrefManager sharedPrefManager;
    private GoogleCloudMessaging gcm;
    String SENDER_ID = "800770961566";

    public GcmManager(NetworkingManager networkingManager, SharedPrefManager sharedPrefManager, GoogleCloudMessaging gcm) {
        this.networkingManager = networkingManager;
        this.sharedPrefManager = sharedPrefManager;
        this.gcm = gcm;
    }

    public void registerInBackground(final int appVersion) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String SENDER_ID = "800770961566";
                try {
                    sendRegistrationIdToBackend(gcm.register(SENDER_ID), appVersion);
                } catch (IOException ex) {
                    Logger.log("GCM# " + "Error :" + ex.getMessage());
                }
                return "";
            }
            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    public void sendRegistrationIdToBackend(final String regid, final int appVersion) {
        AndroidDeviceToken t = new AndroidDeviceToken();
        t.token = regid;
        networkingManager.sendDeviceToken(t, new NetworkCallback<AndroidDeviceToken>() {
            @Override
            public void completionHandler(AndroidDeviceToken object, ServerError error) {
                if (error == null)
                    sharedPrefManager.saveGcm(regid, appVersion);
            }
        });
    }

    public String getRegistrationId(int appVersion) {
        String registrationId = sharedPrefManager.getGcmRegId();
        if (registrationId.isEmpty()) {
            Log.i("MainActivity", "Registration not found.");
            return "";
        }
        int registeredVersion = sharedPrefManager.getGcmAppVersion();
        if (registeredVersion != appVersion) {
            return "";
        }
        return registrationId;
    }
}
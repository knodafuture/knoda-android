package managers;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

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

    public void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String SENDER_ID = "800770961566";
                try {
                    sendRegistrationIdToBackend(gcm.register(SENDER_ID));
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

    public void sendRegistrationIdToBackend(final String regid) {
        AndroidDeviceToken t = new AndroidDeviceToken();
        t.token = regid;
        networkingManager.sendDeviceToken(t, new NetworkCallback<AndroidDeviceToken>() {
            @Override
            public void completionHandler(AndroidDeviceToken object, ServerError error) {
                if (error == null) {
                    sharedPrefManager.saveGcm(regid);
                } else {
                    Logger.log("GCM# " + error.getDescription() + " " + error.statusCode);
                }

            }
        });
    }

    public String getRegistrationId() {
        String registrationId = sharedPrefManager.getGcmRegId();
        if (registrationId.isEmpty()) {
            Log.i("MainActivity", "Registration not found.");
            return "";
        }
        return registrationId;
    }
}
package gcm;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import di.KnodaApplication;
import managers.NetworkingManager;
import managers.UserManager;
import models.Invitation;
import models.Notification;
import models.Prediction;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import views.core.MainActivity;
import views.core.Spinner;
import views.core.SplashActivity;
import views.details.DetailsFragment;
import views.group.GroupSettingsFragment;
import views.predictionlists.AnotherUsersProfileFragment;


public class GcmIntentService extends IntentService {
    public static final String TAG = "gcm.GcmIntentService";
    public static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("gcm.GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                if (KnodaApplication.isActivityVisible()) {
                    showAlert(extras.getString("alert", ""), extras.getString("type"), extras.getString("id"));
                } else {
                    sendNotification(extras.getString("alert", ""), extras.getString("type"), extras.getString("id"));
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void sendNotification(String msg) {
        sendNotification(msg, "", "");
    }

    private void sendNotification(String msg, String type, String id) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent showActivitiesIntent = new Intent(this, SplashActivity.class);
        showActivitiesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showActivitiesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        showActivitiesIntent.putExtra("type", type);
        showActivitiesIntent.putExtra("id", id);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                showActivitiesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(com.knoda.knoda.R.drawable.ic_notification)
                        .setContentTitle("KNODA")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true);


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void showAlert(final String msg) {
        showAlert(msg, "", "");
    }

    private void showAlert(final String msg, final String type, final String notificationId) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                if (!KnodaApplication.alertShowing) {
                    KnodaApplication.alertShowing = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(((KnodaApplication) getApplication()).getCurrentActivity());
                    builder.setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton("Show", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    final MainActivity mainActivity = ((MainActivity) ((KnodaApplication) getApplication()).getCurrentActivity());
                                    final NetworkingManager networkingManager = mainActivity.networkingManager;
                                    UserManager userManager = mainActivity.userManager;
                                    final Spinner spinner = mainActivity.spinner;

                                    final Notification pushNotification = new Notification(notificationId, type);

                                    if (userManager.isLoggedIn()) {
                                        spinner.show();
                                        userManager.refreshUser(new NetworkCallback<User>() {
                                                                    @Override
                                                                    public void completionHandler(User object, ServerError error) {
                                                                        if (error != null) {
                                                                            spinner.hide();
                                                                            return;
                                                                        } else {
                                                                            if (pushNotification.type.equals("p")) {
                                                                                networkingManager.getPrediction(Integer.parseInt(pushNotification.id), new NetworkCallback<Prediction>() {
                                                                                    @Override
                                                                                    public void completionHandler(Prediction object, ServerError error) {
                                                                                        spinner.hide();
                                                                                        if (error != null)
                                                                                            mainActivity.onActivity();
                                                                                        else {
                                                                                            DetailsFragment fragment = DetailsFragment.newInstance(object);
                                                                                            mainActivity.pushFragment(fragment);
                                                                                        }
                                                                                    }
                                                                                });
                                                                            } else if (pushNotification.type.equals("gic")) {
                                                                                networkingManager.getInvitationByCode(pushNotification.id, new NetworkCallback<Invitation>() {
                                                                                    @Override
                                                                                    public void completionHandler(Invitation object, ServerError error) {
                                                                                        spinner.hide();
                                                                                        if (error != null)
                                                                                            mainActivity.onActivity();
                                                                                        else {
                                                                                            GroupSettingsFragment fragment = GroupSettingsFragment.newInstance(object.group, pushNotification.id);
                                                                                            mainActivity.pushFragment(fragment);
                                                                                        }
                                                                                    }
                                                                                });
                                                                            } else if (pushNotification.type.equals("f")) {
                                                                                spinner.hide();
                                                                                AnotherUsersProfileFragment fragment = AnotherUsersProfileFragment.newInstance(Integer.parseInt(pushNotification.id));
                                                                                mainActivity.pushFragment(fragment);
                                                                            } else {
                                                                                mainActivity.onActivity();
                                                                                spinner.hide();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                        );
                                    } else{
                                        userManager.loginAsGuest(new NetworkCallback<User>() {
                                            @Override
                                            public void completionHandler(User object, ServerError error) {
                                                mainActivity.showLogin("Whoa there cowboy!", "You're just a guest.\nSign up with Knoda.");
                                            }
                                        });
                                    }

                                    KnodaApplication.alertShowing = false;
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    KnodaApplication.alertShowing = false;
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }
}
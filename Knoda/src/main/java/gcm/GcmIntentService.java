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
import views.core.MainActivity;

public class GcmIntentService extends IntentService {
    public static final String TAG = "gcm.GcmIntentService";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

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
                    showAlert(extras.getString("alert", ""));
                } else {
                    sendNotification(extras.getString("alert", ""));
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent showActivitiesIntent = new Intent(this, MainActivity.class);
        showActivitiesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        showActivitiesIntent.putExtra("showActivity", true);
        showActivitiesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                showActivitiesIntent, 0);

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

    private void showAlert(final String msg)
    {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                if (!KnodaApplication.alertShowing) {
                    KnodaApplication.alertShowing = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(((KnodaApplication)getApplication()).getCurrentActivity());
                    builder.setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton("Show", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ((MainActivity) ((KnodaApplication) getApplication()).getCurrentActivity()).showActivities();
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
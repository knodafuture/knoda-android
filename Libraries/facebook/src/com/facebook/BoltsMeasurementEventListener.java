package com.facebook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class BoltsMeasurementEventListener extends BroadcastReceiver {
    private final static String MEASUREMENT_EVENT_NOTIFICATION_NAME = "com.parse.bolts.measurement_event";
    private final static String MEASUREMENT_EVENT_NAME_KEY = "event_name";
    private final static String MEASUREMENT_EVENT_ARGS_KEY = "event_args";
    private final static String BOLTS_MEASUREMENT_EVENT_PREFIX = "bf_";
    private static BoltsMeasurementEventListener _instance;
    private Context applicationContext;

    private BoltsMeasurementEventListener(Context context) {
        applicationContext = context.getApplicationContext();
    }

    static BoltsMeasurementEventListener getInstance(Context context) {
        if (_instance != null) {
            return _instance;
        }
        _instance = new BoltsMeasurementEventListener(context);
        _instance.open();
        return _instance;
    }

    private void open() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(applicationContext);
        broadcastManager.registerReceiver(this, new IntentFilter(MEASUREMENT_EVENT_NOTIFICATION_NAME));
    }

    private void close() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(applicationContext);
        broadcastManager.unregisterReceiver(this);
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(context);
        String eventName = BOLTS_MEASUREMENT_EVENT_PREFIX + intent.getStringExtra(MEASUREMENT_EVENT_NAME_KEY);
        appEventsLogger.logEvent(eventName, intent.getBundleExtra(MEASUREMENT_EVENT_ARGS_KEY));
    }
}

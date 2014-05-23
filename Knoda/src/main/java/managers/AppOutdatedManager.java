package managers;

import android.net.Uri;

<<<<<<< HEAD
=======
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

>>>>>>> added alert
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

import pubsub.AppOutdatedEvent;
import views.core.BaseActivity;

/**
 * Created by nick on 5/23/14.
 */
@Singleton
public class AppOutdatedManager {
    private Bus bus;
    private BaseActivity activity;

    @Subscribe
    public void appOutdated(AppOutdatedEvent event) { handleAppOutdated();}

    @Inject
    public AppOutdatedManager(BaseActivity activity, Bus bus) {
        this.activity = activity;
        this.bus = bus;
        bus.register(this);
    }
    private void alertPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("The version of the app you are using is out of date, please update to continue enjoying Knoda.")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                })
                .setNegativeButton("Market", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent marketIntent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=com.knoda.knoda"
                                        + context.getPackageName())
                        );
                        context.startActivity(marketIntent);
                    }
                });
        builder.create().show();
    }

    private void handleAppOutdated() {

        alertPopUp();

    }
}

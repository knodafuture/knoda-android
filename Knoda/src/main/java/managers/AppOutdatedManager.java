package managers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Singleton;

import pubsub.AppOutdatedEvent;
import views.core.BaseActivity;

/**
 * Created by nick on 5/23/14.
 */
@Singleton
public class AppOutdatedManager {
    Bus bus;
    private BaseActivity activity;

    public AppOutdatedManager(BaseActivity activity) {
        this.activity = activity;
    }

    @Subscribe
    public void appOutdated(AppOutdatedEvent event) {
        handleAppOutdated();
    }

    public void setBus(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    private void alertPopUp() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                                            + activity.getPackageName())
                                    //Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.knoda.knoda")//link for amazon
                            );
                            activity.startActivity(marketIntent);
                        }
                    });
            builder.create().show();
        } catch (Exception e) {
        }
    }

    private void handleAppOutdated() {

        alertPopUp();

    }
}

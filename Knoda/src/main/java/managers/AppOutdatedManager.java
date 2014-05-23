package managers;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

import pubsub.AppOutdatedEvent;

/**
 * Created by nick on 5/23/14.
 */
@Singleton
public class AppOutdatedManager {
    private Bus bus;
    private Context context;

    @Subscribe
    public void appOutdated(AppOutdatedEvent event) { handleAppOutdated();}

    @Inject
    public AppOutdatedManager(Context applicationContext, Bus bus) {
        this.context = applicationContext;
        this.bus = bus;
        bus.register(this);
    }


    private void handleAppOutdated() {
        //This is your here.


    }
}

package managers;

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


    private void handleAppOutdated() {
        //This is your here.
    }
}

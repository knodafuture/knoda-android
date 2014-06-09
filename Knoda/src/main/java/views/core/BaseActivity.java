package views.core;

import android.app.Activity;
import android.os.Bundle;

import com.squareup.otto.Bus;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import di.ActivityModule;
import di.KnodaApplication;
import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.TwitterManager;
import managers.UserManager;
import unsorted.ErrorReporter;

/**
 * Created by adamengland on 2/14/14.
 */
public class BaseActivity extends Activity {
    private ObjectGraph activityGraph;
    @Inject
    public Bus bus;
    @Inject
    public NetworkingManager networkingManager;

    @Inject
    public UserManager userManager;

    @Inject
    public SharedPrefManager sharedPrefManager;

    @Inject
    public Spinner spinner;

    @Inject
    public ErrorReporter errorReporter;

    @Inject
    public TwitterManager twitterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        KnodaApplication application = (KnodaApplication) getApplication();
        activityGraph = application.getApplicationGraph().plus(getModules().toArray());
        activityGraph.inject(this);
        super.onCreate(savedInstanceState);
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new ActivityModule(this));
    }

    public void inject(Object object) {
        activityGraph.inject(object);
    }

    @Override protected void onDestroy() {
        activityGraph = null;
        System.gc();
        super.onDestroy();
    }
}

package di;

import android.app.Activity;
import android.app.Application;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import managers.UserManager;

/**
 * Created by nick on 1/13/14.
 */
public class KnodaApplication extends Application {
    public static boolean alertShowing = false;
    private static boolean activityVisible;
    @Inject
    UserManager userManager;
    private ObjectGraph graph;
    private Activity currentActivity = null;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
        graph.inject(this);
    }

    protected List getModules() {
        return Arrays.asList(
                new KnodaModule(this)
        );
    }

    public ObjectGraph getApplicationGraph() {
        return graph;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.currentActivity = mCurrentActivity;
    }
}

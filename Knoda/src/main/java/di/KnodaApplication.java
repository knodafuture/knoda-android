package di;

import android.app.Activity;
import android.app.Application;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import managers.AppOutdatedManager;
import managers.UserManager;

/**
 * Created by nick on 1/13/14.
 */
public class KnodaApplication extends Application {
    private ObjectGraph graph;
    private static boolean activityVisible;
    private Activity currentActivity = null;


    @Inject
    UserManager userManager;

    @Inject
    AppOutdatedManager appOutdatedManager;

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


    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public Activity getCurrentActivity(){
        return currentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.currentActivity = mCurrentActivity;
    }
    public static boolean alertShowing = false;
}

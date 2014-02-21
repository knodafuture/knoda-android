package di;

import android.app.Activity;
import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by nick on 1/13/14.
 */
public class KnodaApplication extends Application {
    private ObjectGraph graph;
    private static boolean activityVisible;
    private Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
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
}

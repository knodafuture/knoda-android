package core.di;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by nick on 1/13/14.
 */
public class KnodaApplication extends Application {
    private ObjectGraph graph;

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





}

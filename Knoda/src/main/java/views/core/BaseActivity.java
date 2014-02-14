package views.core;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.knoda.knoda.R;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;
import dagger.ObjectGraph;
import di.ActivityModule;
import di.KnodaApplication;
import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.UserManager;

/**
 * Created by adamengland on 2/14/14.
 */
public class BaseActivity extends Activity {
    private ObjectGraph activityGraph;
    @Inject
    NetworkingManager networkingManager;

    @Inject
    UserManager userManager;

    @Inject
    SharedPrefManager sharedPrefManager;

    @Optional
    @InjectView(R.id.progress_view)
    public FrameLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KnodaApplication application = (KnodaApplication) getApplication();
        activityGraph = application.getApplicationGraph().plus(getModules().toArray());
        activityGraph.inject(this);
        activityGraph.inject(userManager);
        activityGraph.inject(networkingManager);
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

package views.core;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import models.LoginRequest;
import models.ServerError;
import models.User;
import networking.NetworkCallback;

/**
 * Created by nick on 3/13/14.
 */
public class SplashActivity extends BaseActivity {

    @InjectView(R.id.splash_screen)
    RelativeLayout splashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        final LoginRequest request = sharedPrefManager.getSavedLoginRequest();

        if (request == null) {
            launchMainActivity();
        } else {
            userManager.login(request, new NetworkCallback<User>() {
                @Override
                public void completionHandler(User object, ServerError error) {
                    launchMainActivity();
                }
            });
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("showActivity", getIntent().getBooleanArrayExtra("showActivity"));
        startActivity(new Intent(this, MainActivity.class));
    }
}

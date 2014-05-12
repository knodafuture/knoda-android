package views.core;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import models.ServerError;
import models.User;
import networking.NetworkCallback;

public class SplashActivity extends BaseActivity {

    @InjectView(R.id.splash_screen)
    RelativeLayout splashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        userManager.loginSavedUser(new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                launchMainActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.facebook.AppEventsLogger.activateApp(getApplicationContext(), "455514421245892");
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("showActivity", getIntent().getBooleanArrayExtra("showActivity"));
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

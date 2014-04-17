package views.core;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
            final SplashActivity sa = this;
            userManager.login(request, new NetworkCallback<User>() {
                @Override
                public void completionHandler(User object, ServerError error) {
                    if (error != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(sa);
                        builder.setMessage("Your network connection appears to be down.  Please ensure that you are connected to a wireless network, and try again.")
                                .setCancelable(false)
                                .setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = getBaseContext().getPackageManager()
                                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        launchMainActivity();
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        launchMainActivity();
                    }
                }
            });
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("showActivity", getIntent().getBooleanArrayExtra("showActivity"));
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

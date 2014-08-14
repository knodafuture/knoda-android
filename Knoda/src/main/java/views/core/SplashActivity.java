package views.core;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import models.ServerError;
import models.User;
import networking.NetworkCallback;

public class SplashActivity extends BaseActivity {

    @InjectView(R.id.splash_screen)
    RelativeLayout splashScreen;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefManager.setShouldShowVotingWalkthrough(false);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        intent = new Intent(this, MainActivity.class);
        networkingManager.baseUrl = sharedPrefManager.getAPIurl();
    }


    private boolean showNotConnectedToNetworkDialog(VolleyError error) {

        if (error instanceof NoConnectionError) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        return false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        com.facebook.AppEventsLogger.activateApp(getApplicationContext(), "455514421245892");


        userManager.refreshUser(new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                if (error == null) {
                    sharedPrefManager.setShouldShowVotingWalkthrough(true);
                    launchMainActivity();
                    return;
                } else if (showNotConnectedToNetworkDialog(error.underlyingError))
                    return;

                launchMainActivity();
            }
        });
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        this.setIntent(newIntent);
    }

    private void launchMainActivity() {
        if (getIntent().getStringExtra("type") != null) {
            intent.putExtra("type", getIntent().getStringExtra("type"));
        }
        if (getIntent().getStringExtra("id") != null) {
            intent.putExtra("id", getIntent().getStringExtra("id"));
        }
        Uri targetUri = getIntent().getData();
        if (targetUri != null)
            intent.putExtra("launchInfo", targetUri.toString());
        startActivity(intent);
        finish();
    }
}

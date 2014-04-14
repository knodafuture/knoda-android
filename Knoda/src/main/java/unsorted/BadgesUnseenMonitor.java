package unsorted;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;
import com.tapjoy.TapjoyConnect;

import java.util.ArrayList;

import helpers.TapjoyPPA;
import managers.NetworkingManager;
import models.Badge;
import models.ServerError;
import networking.NetworkListCallback;

public class BadgesUnseenMonitor {
    private Context context;
    private Activity activity;
    private NetworkingManager networkingManager;
    private ImageLoader imageLoader;

    public BadgesUnseenMonitor(Activity activity, NetworkingManager networkingManager) {
        this.activity = activity;
        this.networkingManager = networkingManager;
        this.imageLoader = networkingManager.getImageLoader();
    }

    public void execute() {
        networkingManager.getUnseenBadges(new NetworkListCallback<Badge>() {
            @Override
            public void completionHandler(ArrayList<Badge> object, ServerError error) {
                if (object != null && object.size() > 0) {
                    LayoutInflater li = activity.getLayoutInflater();
                    final View unseenBadgeView = li.inflate(R.layout.view_unseen_badge, null);
                    NetworkImageView imageView = (NetworkImageView) unseenBadgeView.findViewById(R.id.badge_image);
                    imageView.setImageUrl(object.get(0).url, imageLoader);
                    final AlertDialog alert = new AlertDialog.Builder(activity)
                            .setNegativeButton("Thanks", null)
                            .setView(unseenBadgeView)
                            .setTitle("You earned a new badge.")
                            .create();
                    alert.show();
                    checkTapJoy(object);
                }
            }
        });
    }

    public void checkTapJoy(ArrayList<Badge> badges) {
        for (Badge badge : badges) {
            if (badge.name.equals("1_challenge")) {
                TapjoyConnect.getTapjoyConnectInstance().actionComplete(TapjoyPPA.TJC_AGREE_OR_DISAGREE_WITH_A_PREDICTION___ANDROID_PPE);
                return;
            }
        }
    }
}

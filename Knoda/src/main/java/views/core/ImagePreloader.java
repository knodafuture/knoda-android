package views.core;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import managers.NetworkingManager;
import models.Badge;
import models.ServerError;
import networking.NetworkListCallback;
import unsorted.Logger;

/**
 * Created by adamengland on 2/18/14.
 */
class ImagePreloader {
    private NetworkingManager networkingManager;

    public ImagePreloader(NetworkingManager networkingManager) {
        this.networkingManager = networkingManager;
    }

    public void invoke() {
        networkingManager.getAvailableBadges(new NetworkListCallback<Badge>() {
            @Override
            public void completionHandler(ArrayList<Badge> object, ServerError error) {
                if (error == null) {
                    for (Badge b : object) {
                        preloadUrl("http://api-cdn.knoda.com/badges/212/" + b.name + ".png");
                    }
                }
            }
        });
    }

    private void preloadUrl(final String url) {
        networkingManager.getImageLoader().get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                Logger.log("PRELOADER# SUCCESS " + url);
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Logger.log("PRELOADER# ERROR " + url);
            }
        });
    }
}

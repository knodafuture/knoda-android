package views.core;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import managers.NetworkingManager;
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

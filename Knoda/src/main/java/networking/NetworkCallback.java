package networking;

import com.android.volley.VolleyError;

import models.BaseModel;

/**
 * Created by nick on 1/8/14.
 */

public interface NetworkCallback <T extends BaseModel> {

    public void completionHandler (T object, VolleyError error);
}



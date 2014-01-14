package networking;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import models.BaseModel;

/**
 * Created by nick on 1/8/14.
 */

public interface NetworkListCallback <T extends BaseModel> {

    public void completionHandler (ArrayList<T> object, VolleyError error);
}
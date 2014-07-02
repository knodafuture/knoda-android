package networking;

import java.util.ArrayList;

import models.BaseModel;
import models.ServerError;

/**
 * Created by nick on 1/8/14.
 */

public interface NetworkListCallback<T extends BaseModel> {

    public void completionHandler(ArrayList<T> object, ServerError error);
}
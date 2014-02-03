package networking;

import models.BaseModel;
import models.ServerError;

/**
 * Created by nick on 1/8/14.
 */

public interface NetworkCallback <T extends BaseModel> {

    public void completionHandler (T object, ServerError error);
}



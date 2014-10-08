package networking;

import java.util.ArrayList;

import models.ServerError;

/**
 * Created by Jeff 10/7/2014
 */

public interface NetworkListObjectCallback<T> {

    public void completionHandler(ArrayList<T> object, ServerError error);
}
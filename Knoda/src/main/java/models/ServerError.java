package models;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by nick on 1/17/14.
 */
public class ServerError extends BaseModel {

    public Integer statusCode;
    public Map<String, String> headers;
    public ArrayList<FieldError> serverErrors = new ArrayList<FieldError>();

    private ServerError(VolleyError error) {
        if (error.networkResponse != null)
        this.statusCode = error.networkResponse.statusCode;
        this.headers = error.networkResponse.headers;
    }

    public static ServerError newInstanceWithVolleyError(VolleyError error) {
        if (error == null)
            return null;
        else
            return new ServerError(error);

    }

    public String getDescription() {
        return "empty description";
    }

}


class FieldError extends BaseModel {

    public String field;
    public ArrayList<String> reasons = new ArrayList<String>();

}
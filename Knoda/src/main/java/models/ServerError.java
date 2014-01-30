package models;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Map;

import core.Logger;

/**
 * Created by nick on 1/17/14.
 */
public class ServerError extends BaseModel {

    public Integer statusCode;
    public Map<String, String> headers;
    public ArrayList<FieldError> serverErrors = new ArrayList<FieldError>();
    public Throwable cause;

    private ServerError(VolleyError error) {
        try {
            this.statusCode = error.networkResponse.statusCode;
            this.headers = error.networkResponse.headers;
            this.cause = error.getCause();
        } catch (NullPointerException ex) {
            Logger.log("Error creating server error" + ex.getCause());
        }
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
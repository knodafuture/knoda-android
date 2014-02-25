package models;

import com.android.volley.VolleyError;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import unsorted.Logger;

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
            parseErrors(error);
        } catch (NullPointerException ex) {
            Logger.log("Error creating server error" + ex.getCause());
        } catch (JSONException ex) {
            Logger.log("JSON exception" + ex);
        }
    }

    public static ServerError newInstanceWithVolleyError(VolleyError error) {
        if (error == null)
            return null;
        else {
            Logger.log(error.toString());
            return new ServerError(error);
        }

    }

    public String getDescription() {
        if (serverErrors == null || serverErrors.size() == 0)
            return "Unkown error. Please try again later.";

        FieldError firstError = serverErrors.get(0);
        return WordUtils.capitalizeFully(firstError.field) + " " + firstError.reasons.get(0);

    }

    private void parseErrors(VolleyError error) throws JSONException {
        String responseBody = new String(error.networkResponse.data);

        if (responseBody == null)
            return;


        JSONObject obj = new JSONObject(responseBody);

        if (obj == null)
            return;

        JSONObject errors = obj.getJSONObject("errors");

        Iterator<?> keys = errors.keys();
        while( keys.hasNext() ){
            String key = (String)keys.next();
            JSONArray reasons = errors.getJSONArray(key);
            if (reasons == null)
                continue;
            FieldError newError = new FieldError();
            newError.field = key;
            for (int i = 0; i < reasons.length(); i++) {
                newError.reasons.add(reasons.get(i).toString());
            }
            serverErrors.add(newError);
        }
    }

}


class FieldError extends BaseModel {

    public String field;
    public ArrayList<String> reasons = new ArrayList<String>();
}
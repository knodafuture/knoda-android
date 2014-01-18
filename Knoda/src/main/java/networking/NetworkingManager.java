package networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import core.Logger;
import models.BaseModel;
import models.LoginRequest;
import models.LoginResponse;
import models.ServerError;
import models.Topics;


/**
 * Created by nick on 1/8/14.
 */


@Singleton
public class NetworkingManager {

    Context context;

    private static RequestQueue mRequestQueue;

    private HashMap<String, String> headers;

    @Inject
    public NetworkingManager (Context applicationContext) {
        this.context = applicationContext;
        mRequestQueue = Volley.newRequestQueue(context);
    }


    public void getTopics (final NetworkCallback<Topics> callback) {
        String url = "http://api-test.knoda.com/api/topics.json?auth_token=7GPuMMaf41qMWqaAuoQZ";

        //getResource(url, Topics.class, callback);

    }

    public void login (final LoginRequest payload, final NetworkCallback<LoginResponse> callback) {

        String url = "http://api-dev.knoda.com/api/session.json";

        executeRequest(Request.Method.POST, url, payload, LoginResponse.class, callback);

    }

    private Map<String, String> getHeaders() {

        if (headers == null) {
            headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json; charset=utf-8;");
            headers.put("Accept", "application/json; api_version=2");
        }

        Logger.log("using headers" + headers.toString());
        return headers;
    }


    private <T extends BaseModel> void executeRequest (int httpMethod, String url, final BaseModel payload, final Class responseClass, final NetworkCallback<T> callback) {
        Response.Listener<T> responseListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T t) {
                callback.completionHandler(t, null);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.completionHandler(null, ServerError.newInstanceWithVolleyError(volleyError));
            }
        };

        GsonRequest<T> request = new GsonRequest<T>(httpMethod, url, responseClass, getHeaders(), responseListener, errorListener);

        if (payload != null)
            request.setPayload(payload);

        mRequestQueue.add(request);
    }

    private <T extends BaseModel> void executeListRequest (int httpMethod, final String url, final BaseModel payload, final Class responseClass, final NetworkListCallback<T> callback) {

        Response.Listener<ArrayList<T>> responseListener = new Response.Listener<ArrayList<T>>() {
            @Override
            public void onResponse(ArrayList<T> response) {
                callback.completionHandler(response, null);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.completionHandler(null, ServerError.newInstanceWithVolleyError(volleyError));
            }
        };

        GsonRequest<ArrayList<T>> request = new GsonRequest<ArrayList<T>>(httpMethod, url, responseClass , getHeaders(), responseListener, errorListener);

        if (payload != null)
            request.setPayload(payload);

        mRequestQueue.add(request);

    }
}

package networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.ArrayList;

import models.BaseModel;
import models.Topics;


/**
 * Created by nick on 1/8/14.
 */


@Singleton
public class NetworkingManager {

    @Inject
    Gson gson;

    private Provider<Context> contextProvider;

    private static RequestQueue mRequestQueue;

    @Inject
    public NetworkingManager (Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
        mRequestQueue = Volley.newRequestQueue(contextProvider.get());
    }


    public void getTopics (final NetworkCallback<Topics> callback) {
        String url = "http://api-test.knoda.com/api/topics.json?auth_token=7GPuMMaf41qMWqaAuoQZ";

        getResource(url, Topics.class, callback);

    }



    private <T extends BaseModel> void getResourceList (String url, final Class tClass, final NetworkListCallback<T> callback) {

        Response.Listener<ArrayList<T>> responseListener = new Response.Listener<ArrayList<T>>() {
            @Override
            public void onResponse(ArrayList<T> response) {
                callback.completionHandler(response, null);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.completionHandler(null, volleyError);
            }
        };

        GsonRequest<ArrayList<T>> request = new GsonRequest<ArrayList<T>>(Request.Method.GET, url, tClass , null, responseListener, errorListener);

        mRequestQueue.add(request);
    }


    private <T extends BaseModel> void getResource (String url, final Class tClass, final NetworkCallback<T> callback) {

        Response.Listener<T> responseListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T t) {
                callback.completionHandler(t, null);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.completionHandler(null, volleyError);
            }
        };

        GsonRequest<T> request = new GsonRequest<T>(Request.Method.GET, url, tClass, null, responseListener, errorListener);

        mRequestQueue.add(request);
    }
}

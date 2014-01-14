package networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.BaseModel;
import models.Topics;


/**
 * Created by nick on 1/8/14.
 */


@Singleton
public class NetworkingManager {

    Context context;

    private static RequestQueue mRequestQueue;

    @Inject
    public NetworkingManager (Context applicationContext) {
        this.context = applicationContext;
        mRequestQueue = Volley.newRequestQueue(context);
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

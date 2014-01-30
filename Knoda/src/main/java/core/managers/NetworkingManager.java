package core.managers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import builders.ParamBuilder;
import core.Logger;
import core.networking.BitmapLruCache;
import core.networking.GsonArrayRequest;
import core.networking.GsonRequest;
import core.networking.NetworkCallback;
import core.networking.NetworkListCallback;
import Factories.TypeTokenFactory;
import models.BaseModel;
import models.LoginRequest;
import models.LoginResponse;
import models.Prediction;
import models.ServerError;
import models.SignUpRequest;
import models.User;


/**
 * Created by nick on 1/8/14.
 */


@Singleton
public class NetworkingManager {

    Context context;

    private static RequestQueue mRequestQueue;

    private HashMap<String, String> headers;

    public static String termsOfServiceUrl = "http://knoda.com/terms";
    public static String privacyPolicyUrl = "http://knoda.com/privacy";
    public static Integer PAGE_LIMIT = 50;
    public static String baseUrl = "http://api-test.knoda.com/api/";

    private ImageLoader imageLoader;

    @Inject SharedPrefManager sharedPrefManager;

    @Inject
    public NetworkingManager (Context applicationContext) {
        this.context = applicationContext;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public ImageLoader getImageLoader() {
        if (imageLoader == null)
            imageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache());
        return imageLoader;
    }

    public void login (final LoginRequest payload, final NetworkCallback<LoginResponse> callback) {

        String url = buildUrl("session.json", false, null);

        executeRequest(Request.Method.POST, url, payload, LoginResponse.class, callback);

    }

    public void signup(final SignUpRequest payload, final NetworkCallback<LoginResponse> callback) {

        String url = buildUrl("registration.json", false, null);

        executeRequest(Request.Method.POST, url, payload, LoginResponse.class, callback);

    }

    public void getCurrentUser(final NetworkCallback<User> callback) {
        String url = buildUrl("profile.json", true, null);

        executeRequest(Request.Method.GET, url, null, User.class, callback);
    }


    public void getPredictionsAfter(final Integer lastId, final NetworkListCallback<Prediction> callback) {
        ParamBuilder builder = ParamBuilder.create().add("recent", "true").add("limit", PAGE_LIMIT.toString()).withLastId(lastId);

        String url = buildUrl("predictions.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getPredictionListTypeToken(), callback);
    }

    private Map<String, String> getHeaders() {

        if (headers == null) {
            headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json; charset=utf-8;");
            headers.put("Accept", "application/json; api_version=2;");
        }

        Logger.log("using headers" + headers.toString());
        return headers;
    }


    private String buildUrl(String path, boolean requiresAuthToken, ParamBuilder paramBuilder) {

        if (requiresAuthToken) {
            if (paramBuilder == null)
                paramBuilder = ParamBuilder.create();
            String authToken = getAuthToken();
            if (authToken == null) {
                throw new RuntimeException("No auth token found");
            }

            paramBuilder.add("auth_token", authToken);
        }

        String url = baseUrl + path;
        if (paramBuilder != null)
            url += paramBuilder.build();

        return url;

    }

    private String getAuthToken() {
        return sharedPrefManager.getSavedAuthtoken();
    }


    private <T extends BaseModel> void executeRequest (int httpMethod, String url, final BaseModel payload, final Class responseClass, final NetworkCallback<T> callback) {

        Logger.log("Executing request" + url);
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

    private <T extends BaseModel> void executeListRequest (int httpMethod, final String url, final BaseModel payload, final TypeToken token, final NetworkListCallback<T> callback) {
        Logger.log("Executing request" + url);

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

        GsonArrayRequest<ArrayList<T>> request = new GsonArrayRequest<ArrayList<T>>(httpMethod, url, token , getHeaders(), responseListener, errorListener);

        if (payload != null)
            request.setPayload(payload);

        mRequestQueue.add(request);

    }
}
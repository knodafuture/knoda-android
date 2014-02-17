package managers;

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
import factories.TypeTokenFactory;
import models.ActivityItem;
import models.Badge;
import models.BaseModel;
import models.Challenge;
import models.LoginRequest;
import models.LoginResponse;
import models.PasswordChangeRequest;
import models.Prediction;
import models.ServerError;
import models.SignUpRequest;
import models.Tag;
import models.User;
import networking.BitmapLruCache;
import networking.GsonArrayRequest;
import networking.GsonRequest;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import unsorted.Logger;


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
    public static Integer PAGE_LIMIT = 25;
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

    public void signout(final NetworkCallback<User> callback) {
        String url = buildUrl("session.json", true, null);
        executeRequest(Request.Method.DELETE, url, null, User.class, callback);
    }

    public void getCurrentUser(final NetworkCallback<User> callback) {
        String url = buildUrl("profile.json", true, null);

        executeRequest(Request.Method.GET, url, null, User.class, callback);
    }

    public void getPredictionsWithTagAfter(final Tag tag, final Integer lastId, final NetworkListCallback<Prediction> callback) {
        ParamBuilder builder = ParamBuilder.create().withPageLimit().add("recent", "true").withLastId(lastId);

        if (tag != null)
            builder.add("tag", tag.name);

        String url = buildUrl("predictions.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getPredictionListTypeToken(), callback);
    }

    public void getPredictionsAfter(final Integer lastId, final NetworkListCallback<Prediction> callback) {
        getPredictionsWithTagAfter(null, lastId, callback);
    }

    public void getChallengeForPrediction(final Integer predictionId, final NetworkCallback<Challenge> callback) {
        ParamBuilder builder = ParamBuilder.create().add("prediction_id", predictionId.toString());

        String url = buildUrl("predictions/" + predictionId + "/challenge.json", true, builder);

        executeRequest(Request.Method.GET, url, null, Challenge.class, callback);
    }

    public void agreeWithPrediction(final Integer predictionId, final NetworkCallback<Challenge> callback) {

        ParamBuilder builder = new ParamBuilder().create().add("prediction_id", predictionId.toString());

        String url = buildUrl("predictions/" + predictionId + "/agree.json", true, builder);

        executeRequest(Request.Method.POST, url, null, Challenge.class, new NetworkCallback<Challenge>() {
            @Override
            public void completionHandler(Challenge object, ServerError error) {
                if (error != null)
                    callback.completionHandler(null, error);
                else
                    getChallengeForPrediction(predictionId, callback);
            }
        });
    }

    public void disagreeWithPrediction(final Integer predictionId, final NetworkCallback<Challenge> callback) {

        ParamBuilder builder = new ParamBuilder().create().add("prediction_id", predictionId.toString());

        String url = buildUrl("predictions/" + predictionId + "/disagree.json", true, builder);

        executeRequest(Request.Method.POST, url, null, Challenge.class, new NetworkCallback<Challenge>() {
            @Override
            public void completionHandler(Challenge object, ServerError error) {
                if (error != null) {
                    callback.completionHandler(null, error);
                } else
                    getChallengeForPrediction(predictionId, callback);
            }
        });
    }

    public void getActivityItemsAfter(final Integer lastId, NetworkListCallback<ActivityItem> callback) {

        ParamBuilder builder = new ParamBuilder().create().withLastId(lastId).withPageLimit();

        String url = buildUrl("activityfeed.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getActivityItemTypeToken(), callback);

    }

    public void getHistoryAfter(final Integer lastId, NetworkListCallback<Prediction> callback) {

        ParamBuilder builder = new ParamBuilder().create().withLastId(lastId).withPageLimit().add("challenged", "true");

        String url = buildUrl("predictions.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getPredictionListTypeToken(), callback);
    }

    public void getUser(final Integer userId, NetworkCallback<User> callback) {
        String url = buildUrl("users/" + userId + ".json", true, null);

        executeRequest(Request.Method.GET, url, null, User.class, callback);
    }

    public void getPredictionsForUserAfter(final Integer userId, final Integer lastId, NetworkListCallback<Prediction> callback) {
        ParamBuilder builder = new ParamBuilder().create().withLastId(lastId).withPageLimit();

        String url = buildUrl("users/" + userId + "/predictions.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getPredictionListTypeToken(), callback);
    }


    public void getPrediction(final Integer predictionId, final NetworkCallback<Prediction> callback) {

        String url = buildUrl("predictions/" + predictionId + ".json", true, null);

        executeRequest(Request.Method.GET, url, null, Prediction.class, callback);

    }

    public void submitPrediction(final Prediction prediction, final NetworkCallback<Prediction> callback) {

        String url = buildUrl("predictions.json", true, null);

        executeRequest(Request.Method.POST, url, prediction, Prediction.class, callback);
    }

    public void getTags(NetworkListCallback<Tag> callback) {
        String url = buildUrl("topics.json", true, null);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getTopicListTypeToken(), callback);
    }

    public void searchForUsers(String searchTerm, NetworkListCallback<User> callback) {
        ParamBuilder builder = ParamBuilder.create().add("limit", "5").add("q", searchTerm);

        String url = buildUrl("search/users.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getUserListTypeToken(), callback);
    }

    public void searchForPredictions(String searchterm, NetworkListCallback<Prediction> callback) {
        ParamBuilder builder = ParamBuilder.create().add("limit", "5").add("q", searchterm);

        String url = buildUrl("search/predictions.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getPredictionListTypeToken(), callback);
    }

    public void updateUser(User user, final NetworkCallback<User> callback) {
        String url = buildUrl("profile.json", true, null);
        executeRequest(Request.Method.PUT, url, user, User.class, callback);
    }

    public void changePassword(PasswordChangeRequest passwordChange, final NetworkCallback<User> callback) {
        String url = buildUrl("password.json", true, null);
        executeRequest(Request.Method.PUT, url, passwordChange, User.class, callback);
    }

    public void getBadges(final NetworkListCallback<Badge> callback) {
        String url = buildUrl("badges.json", true, null);
        Logger.log("Badge url: " + url);
        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getBadgeListTypeToken(), callback);
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
                Logger.log("RESPONSE RECEIVED.");
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

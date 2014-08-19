package managers;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;

import org.apache.http.entity.ContentType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import builders.MultipartRequestBuilder;
import builders.ParamBuilder;
import factories.GsonF;
import factories.TypeTokenFactory;
import models.ActivityItem;
import models.AndroidDeviceToken;
import models.BaseModel;
import models.Challenge;
import models.Comment;
import models.Contest;
import models.ContestUser;
import models.ForgotPasswordRequest;
import models.Group;
import models.GroupInvitation;
import models.Invitation;
import models.JoinGroupRequest;
import models.Leader;
import models.LoginRequest;
import models.LoginResponse;
import models.Member;
import models.PasswordChangeRequest;
import models.Prediction;
import models.ServerError;
import models.Setting;
import models.SettingsCategory;
import models.SignUpRequest;
import models.SocialAccount;
import models.Tag;
import models.User;
import models.UserContact;
import models.UserContacts;
import networking.BitmapLruCache;
import networking.GsonArrayRequest;
import networking.GsonRequest;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import unsorted.Logger;

@Singleton
public class NetworkingManager {

    public static String termsOfServiceUrl = "http://knoda.com/terms";
    public static String privacyPolicyUrl = "http://knoda.com/privacy";
    public static String supportUrl = "http://knoda.com/support";
    public static Integer PAGE_LIMIT = 50;
    public static String baseUrl = "http://captaincold.knoda.com/api/";
    private static RequestQueue mRequestQueue;
    Context context;
    String api_version = "5";
    @Inject
    SharedPrefManager sharedPrefManager;
    @Inject
    Bus bus;
    int timeout = 15;//timeout in seconds
    private HashMap<String, String> headers;
    private ImageLoader imageLoader;

    @Inject
    public NetworkingManager(Context applicationContext) {
        this.context = applicationContext;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public ImageLoader getImageLoader() {
        if (imageLoader == null)
            imageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache());
        return imageLoader;
    }

    public void login(final LoginRequest payload, final NetworkCallback<LoginResponse> callback) {

        String url = buildUrl("session.json", false, null);

        executeRequest(Request.Method.POST, url, payload, LoginResponse.class, callback);

    }

    public void socialSignIn(final SocialAccount payload, final NetworkCallback<LoginResponse> callback) {
        String url = buildUrl("session.json", true, null);
        executeRequestWithTimeout(Request.Method.POST, url, payload, LoginResponse.class, callback, 30);
    }

    public void deleteSocialAccount(final SocialAccount socialAccount, final NetworkCallback<SocialAccount> callback) {
        String url = buildUrl("social_accounts/" + socialAccount.id + ".json", true, null);
        executeRequest(Request.Method.DELETE, url, null, SocialAccount.class, callback);
    }

    public void createSocialAccount(final SocialAccount socialAccount, final NetworkCallback<SocialAccount> callback) {
        String url = buildUrl("social_accounts.json", true, null);
        executeRequest(Request.Method.POST, url, socialAccount, SocialAccount.class, callback);
    }

    public void updateSocialAccount(final SocialAccount socialAccount, final NetworkCallback<SocialAccount> callback) {
        String url = buildUrl("social_accounts/" + socialAccount.id + ".json", true, null);
        executeRequest(Request.Method.PUT, url, socialAccount, SocialAccount.class, callback);
    }

    public void signup(final SignUpRequest payload, final NetworkCallback<LoginResponse> callback) {
        String url = buildUrl("registration.json", true, null);
        executeRequest(Request.Method.POST, url, payload, LoginResponse.class, callback);
    }

    public void signout(final NetworkCallback<User> callback) {
        String url = buildUrl("session.json", true, null);
        executeRequest(Request.Method.DELETE, url, null, User.class, callback);
    }

    public void getCurrentUser(final NetworkCallback<User> callback) {
        try {
            String url = buildUrl("profile.json", true, null);
            executeRequest(Request.Method.GET, url, null, User.class, callback);
        } catch (Exception e) {
            callback.completionHandler(null, ServerError.newInstanceWithVolleyError(new VolleyError("Unable to get current user")));
        }
    }

    public void getPredictionsWithTagAfter(final String tag, final Integer lastId, final NetworkListCallback<Prediction> callback) {
        ParamBuilder builder = ParamBuilder.create().withPageLimit().add("recent", "true").withLastId(lastId);

        if (tag != null)
            builder.add("tag", tag);

        String url = buildUrl("predictions.json", false, builder);
        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getPredictionListTypeToken(), callback);
    }

    public void getPredictionsAfterId(final boolean challenged, Integer lastId, final NetworkListCallback<Prediction> callback) {
        String url;
        ParamBuilder builder = ParamBuilder.create().withPageLimit().withLastId(lastId);
        if (challenged) {
            builder.add("challenged", "true");
        }
        url = buildUrl("predictions.json", false, builder);

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

    public void agreeWithPrediction(final Integer predictionId, final NetworkCallback<Prediction> callback) {

        ParamBuilder builder = new ParamBuilder().create().add("prediction_id", predictionId.toString());

        String url = buildUrl("predictions/" + predictionId + "/agree.json", true, builder);

        executeRequest(Request.Method.POST, url, null, Challenge.class, new NetworkCallback<Challenge>() {
            @Override
            public void completionHandler(Challenge object, ServerError error) {
                if (error != null)
                    callback.completionHandler(null, error);
                else
                    getPrediction(predictionId, callback);
            }
        });
    }

    public void disagreeWithPrediction(final Integer predictionId, final NetworkCallback<Prediction> callback) {

        ParamBuilder builder = new ParamBuilder().create().add("prediction_id", predictionId.toString());

        String url = buildUrl("predictions/" + predictionId + "/disagree.json", true, builder);

        executeRequest(Request.Method.POST, url, null, Challenge.class, new NetworkCallback<Challenge>() {
            @Override
            public void completionHandler(Challenge object, ServerError error) {
                if (error != null) {
                    callback.completionHandler(null, error);
                } else
                    getPrediction(predictionId, callback);
            }
        });
    }

    public void sendBS(final Integer predictionId, final NetworkCallback<Prediction> callback) {
        String url = buildUrl("predictions/" + predictionId + "/bs.json", true, null);

        executeRequest(Request.Method.POST, url, null, Prediction.class, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                if (error != null)
                    callback.completionHandler(object, error);
                else
                    getPrediction(predictionId, callback);
            }
        });
    }

    public void sendOutcome(final Integer predictionId, final boolean outcome, final NetworkCallback<Prediction> callback) {
        String path;
        if (outcome)
            path = "predictions/" + predictionId + "/realize.json";
        else
            path = "predictions/" + predictionId + "/unrealize.json";

        String url = buildUrl(path, true, null);

        executeRequest(Request.Method.POST, url, null, Prediction.class, callback);
    }

    public void updatePrediction(final Prediction prediction, final NetworkCallback<Prediction> callback) {

        String url = buildUrl("predictions/" + prediction.id + ".json", true, null);

        executeRequest(Request.Method.PUT, url, prediction, Prediction.class, callback);
    }

    public void getActivityItemsAfter(final Integer lastId, String filter, NetworkListCallback<ActivityItem> callback) {

        ParamBuilder builder = new ParamBuilder().create().withLastId(lastId).withPageLimit();
        if (filter != null)
            builder.add("filter", filter);

        String url = buildUrl("activityfeed.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getActivityItemTypeToken(), callback);

    }

    public void getUnseenActivityItems(String filter, final NetworkListCallback<ActivityItem> callback) {

        ParamBuilder builder = ParamBuilder.create().add("list", "unseen");
        if (filter != null)
            builder.add("filter", filter);

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

    public void getPredictionsForGroupAfter(final Integer groupId, final Integer lastId, NetworkListCallback<Prediction> callback) {
        ParamBuilder builder = new ParamBuilder().create().withLastId(lastId).withPageLimit();
        String url = buildUrl("groups/" + groupId + "/predictions.json", true, builder);
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
        ParamBuilder builder = ParamBuilder.create().withPageLimit().add("q", searchterm);

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

    public void getCommentsForPredictionWithLastId(Integer predictionId, Integer lastId, NetworkListCallback<Comment> callback) {

        ParamBuilder builder = ParamBuilder.create().withLastIdGt(lastId).add("list", "prediction").add("prediction_id", predictionId.toString()).withPageLimit();

        String url = buildUrl("comments.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getCommentListTypeToken(), callback);
    }

    public void getAgreedUsers(Integer predictionId, NetworkListCallback<User> callback) {

        String url = buildUrl("predictions/" + predictionId + "/history_agreed.json", true, null);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getUserListTypeToken(), callback);
    }

    public void getDisagreedUsers(Integer predictionId, NetworkListCallback<User> callback) {
        String url = buildUrl("predictions/" + predictionId + "/history_disagreed.json", true, null);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getUserListTypeToken(), callback);
    }

    public void uploadUserAvatar(final File avatarFile, final NetworkCallback<User> callback) {
        String url = buildUrl("profile.json", true, null);
        MultipartRequestBuilder builder = MultipartRequestBuilder.create().forUrl(url);
        builder.addErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.completionHandler(null, ServerError.newInstanceWithVolleyError(volleyError));
            }
        });
        builder.addListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                User u = GsonF.actory().fromJson(s, User.class);
                callback.completionHandler(u, null);
            }
        });
        builder.addFilePart("user[avatar]", avatarFile, ContentType.APPLICATION_OCTET_STREAM, "image.jpg");
        mRequestQueue.add(builder.build());
    }

    public void uploadGroupAvatar(final Integer groupId, final File avatarFile, final NetworkCallback<Group> callback) {
        String url = buildUrl("groups/" + groupId.toString() + ".json", true, null);
        MultipartRequestBuilder builder = MultipartRequestBuilder.create().forUrl(url);
        builder.addErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.completionHandler(null, ServerError.newInstanceWithVolleyError(volleyError));
            }
        });
        builder.addListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Group g = GsonF.actory().fromJson(s, Group.class);
                callback.completionHandler(g, null);
            }
        });
        builder.addFilePart("avatar", avatarFile, ContentType.APPLICATION_OCTET_STREAM, "image.jpg");
        mRequestQueue.add(builder.build());
    }

    public void sendDeviceToken(final AndroidDeviceToken token, final NetworkCallback<AndroidDeviceToken> callback) {
        String url = buildUrl("android_device_tokens.json", true, null);
        executeRequest(Request.Method.POST, url, token, AndroidDeviceToken.class, new NetworkCallback<AndroidDeviceToken>() {
            @Override
            public void completionHandler(AndroidDeviceToken object, ServerError error) {
                callback.completionHandler(object, error);
            }
        });
    }

    public void addComment(final Comment comment, final NetworkCallback<Comment> callback) {

        String url = buildUrl("comments.json", true, null);
        executeRequest(Request.Method.POST, url, comment, Comment.class, callback);
    }

    public void sendForgotPasswordRequest(final ForgotPasswordRequest request, final NetworkCallback<BaseModel> callback) {
        String url = buildUrl("password.json", false, null);

        executeRequest(Request.Method.POST, url, request, BaseModel.class, callback);
    }

    public void getGroups(NetworkListCallback<Group> callback) {
        String url = buildUrl("groups.json", true, null);
        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getGroupListTypeToken(), callback);
    }

    public void getGroup(int groupId, NetworkCallback<Group> callback) {
        String url = buildUrl("groups/" + groupId + ".json", true, null);
        executeRequest(Request.Method.GET, url, null, Group.class, callback);
    }

    public void submitGroup(final Group group, final NetworkCallback<Group> callback) {
        String url = buildUrl("groups.json", true, null);
        executeRequest(Request.Method.POST, url, group, Group.class, callback);
    }

    public void updateGroup(final Group group, final NetworkCallback<Group> callback) {
        String url = buildUrl("groups/" + group.id + ".json", true, null);
        executeRequest(Request.Method.PUT, url, group, Group.class, callback);
    }

    public void getGroupLeaderboard(final Integer groupId, final String board, final NetworkListCallback<Leader> callback) {
        ParamBuilder builder = new ParamBuilder().create().add("board", board.toLowerCase());
        String url = buildUrl("groups/" + groupId + "/leaderboard.json", true, builder);
        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getLeaderListTypeToken(), callback);
    }

    public void getInvitationByCode(final String code, final NetworkCallback<Invitation> callback) {
        String url = buildUrl("invitations/" + code + ".json", true, null);
        executeRequest(Request.Method.GET, url, null, Invitation.class, callback);
    }

    public void getMembersInGroup(final Integer groupId, final NetworkListCallback<Member> callback) {
        String url = buildUrl("groups/" + groupId + "/memberships.json", true, null);
        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getMemberListTypeToken(), callback);
    }

    public void deleteMembership(final Integer membershipid, final NetworkCallback<Member> callback) {
        String url = buildUrl("memberships/" + membershipid + ".json", true, null);
        executeRequest(Request.Method.DELETE, url, null, Member.class, callback);
    }

    public void autoCompleteUsers(final String query, final NetworkListCallback<User> callback) {
        ParamBuilder builder = ParamBuilder.create().add("query", query);

        String url = buildUrl("users/autocomplete.json", true, builder);
        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getUserListTypeToken(), callback);
    }

    public void sendInvitations(final ArrayList<GroupInvitation> invitations, final NetworkCallback<GroupInvitation> callback) {
        String url = buildUrl("invitations.json", true, null);
        executeRequest(Request.Method.POST, url, invitations, GroupInvitation.class, callback);
    }

    public void joinGroup(final String invitationCode, final Integer groupId, final NetworkCallback<Member> callback) {
        String url = buildUrl("memberships.json", true, null);
        JoinGroupRequest obj = new JoinGroupRequest();
        obj.code = invitationCode;
        obj.groupId = groupId;
        executeRequest(Request.Method.POST, url, obj, Member.class, callback);
    }

    public void sharePredictionOnFacebook(final Prediction prediction, final NetworkCallback<BaseModel> callback) {
        ParamBuilder builder = ParamBuilder.create();
        builder.add("prediction_id", prediction.id.toString());
        String url = buildUrl("facebook.json", true, builder);

        executeRequest(Request.Method.POST, url, null, BaseModel.class, callback);
    }

    public void sharePredictionOnTwitter(final Prediction prediction, final NetworkCallback<BaseModel> callback) {
        ParamBuilder builder = ParamBuilder.create();
        builder.add("prediction_id", prediction.id.toString());
        String url = buildUrl("twitter.json", true, builder);

        executeRequest(Request.Method.POST, url, null, BaseModel.class, callback);
    }

    public void changeSetting(Setting setting, final NetworkCallback<Setting> callback) {
        String url = buildUrl("notification_settings/" + setting.id + ".json", true, null);
        executeRequest(Request.Method.PUT, url, setting, Setting.class, callback);
    }

    public void getSettings(final NetworkListCallback<SettingsCategory> callback) {
        String url = buildUrl("settings.json", true, null);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getSettingsTypeToken(), callback);
    }

    public void getContest(int id, final NetworkCallback<Contest> callback) {
        String url = buildUrl("contests/" + id + ".json", true, null);
        executeRequest(Request.Method.GET, url, null, Contest.class, callback);
    }

    public void getContests(String filter, final NetworkListCallback<Contest> callback) {
        ParamBuilder builder = ParamBuilder.create();
        if (filter != null)
            builder.add("list", filter);
        String url = buildUrl("contests.json", true, builder);

        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getContestsTypeToken(), callback);
    }

    public void getContestsPredictions(int contestId, boolean expired, final NetworkListCallback<Prediction> callback) {
        ParamBuilder builder = ParamBuilder.create();
        if (expired)
            builder.add("list", "expired");

        String url = buildUrl("contests/" + contestId + "/predictions.json", true, builder);
        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getPredictionListTypeToken(), callback);
    }

    public void getContestLeaderboard(int contestId, Integer stage, final NetworkListCallback<ContestUser> callback) {
        ParamBuilder builder = ParamBuilder.create();
        if (stage != null)
            builder.add("stage", stage + "");

        String url = buildUrl("contests/" + contestId + "/leaderboard.json", true, builder);
        executeListRequest(Request.Method.GET, url, null, TypeTokenFactory.getContestUserTypeToken(), callback);
    }

    public void loginAsGuest(final NetworkCallback<LoginResponse> callback) {
        String url = buildUrl("users.json", false, null);

        executeRequest(Request.Method.POST, url, null, LoginResponse.class, callback);
    }

    public void matchPhoneContacts(UserContacts contacts, final NetworkListCallback<UserContact> callback) {
        String url = buildUrl("contact_matches.json", false, null);
        executeListRequest(Request.Method.POST, url, contacts.contacts, TypeTokenFactory.getUserContactTypeToken(), callback);
    }


    private Map<String, String> getHeaders() {

        if (headers == null) {
            headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json; charset=utf-8;");
            headers.put("Accept", "application/json; api_version=" + api_version + ";");
        }

        Logger.log("using headers" + headers.toString());
        return headers;
    }


    private String buildUrl(String path, boolean requiresAuthToken, ParamBuilder paramBuilder) {

        if (requiresAuthToken) {
            if (paramBuilder == null)
                paramBuilder = ParamBuilder.create();
            String authToken = getAuthToken();
            if (authToken != null) {
                paramBuilder.add("auth_token", authToken);
            }
        }

        String url = baseUrl + path;
        if (paramBuilder != null)
            url += paramBuilder.build();
        return url;

    }

    private String getAuthToken() {
        return sharedPrefManager.getSavedAuthtoken();
    }

    private <T extends BaseModel> void executeRequestWithTimeout(int httpMethod, String url, final Object payload, final Class responseClass, final NetworkCallback<T> callback, Integer timeout) {
        Logger.log("Executing request" + url);
        Response.Listener<T> responseListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T t) {
                if (callback != null)
                    callback.completionHandler(t, null);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (checkAndHandleOutdated(volleyError))
                    return;

                if (callback != null)
                    callback.completionHandler(null, ServerError.newInstanceWithVolleyError(volleyError));
            }
        };

        GsonRequest<T> request = new GsonRequest<T>(httpMethod, url, responseClass, getHeaders(), responseListener, errorListener, timeout);

        if (payload != null)
            request.setPayload(payload);

        mRequestQueue.add(request);
    }

    private <T extends BaseModel> void executeRequest(int httpMethod, String url, final Object payload, final Class responseClass, final NetworkCallback<T> callback) {
        executeRequestWithTimeout(httpMethod, url, payload, responseClass, callback, timeout);
    }

    private <T extends BaseModel> void executeListRequest(int httpMethod, final String url, final Object payload, final TypeToken token, final NetworkListCallback<T> callback) {
        Logger.log("Executing request" + url);

        Response.Listener<ArrayList<T>> responseListener = new Response.Listener<ArrayList<T>>() {
            @Override
            public void onResponse(ArrayList<T> response) {
                if (callback != null)
                    callback.completionHandler(response, null);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (checkAndHandleOutdated(volleyError))
                    return;
                if (callback != null)
                    callback.completionHandler(null, ServerError.newInstanceWithVolleyError(volleyError));
            }
        };

        GsonArrayRequest<ArrayList<T>> request = new GsonArrayRequest<ArrayList<T>>(httpMethod, url, token, getHeaders(), responseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(timeout * 1000, 0, 2.0f));

        if (payload != null)
            request.setPayload(payload);

        mRequestQueue.add(request);
    }

    private boolean checkAndHandleOutdated(VolleyError error) {
//        if (error.networkResponse.statusCode == 410) {
//            bus.post(new AppOutdatedEvent());
//            return true;
//        }
//
//        return false;

        return false;
    }
}

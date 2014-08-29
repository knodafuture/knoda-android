package managers;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.BaseModel;
import models.Prediction;
import models.ServerError;
import models.SocialAccount;
import models.User;
import networking.NetworkCallback;
import unsorted.Logger;

/**
 * Created by nick on 5/11/14.
 */
@Singleton
public class FacebookManager {

    private static ArrayList<NetworkCallback<SocialAccount>> callbacks = new ArrayList<NetworkCallback<SocialAccount>>();
    private UserManager userManager;
    private NetworkingManager networkingManager;

    @Inject
    public FacebookManager(UserManager userManager, NetworkingManager networkingManager) {
        this.userManager = userManager;
        this.networkingManager = networkingManager;

        if (this.userManager == null || this.networkingManager == null)
            Logger.log("__________ UH OH SHIT IS NULL ________________");
    }

    private static Session openActiveSession(Activity activity, boolean allowLoginUI, Session.StatusCallback callback, List<String> permissions) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).setPermissions(permissions).setCallback(callback);
        Session session = new Session.Builder(activity).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
            Session.setActiveSession(session);
            session.openForRead(openRequest);
            return session;
        }
        return null;
    }

    public void openSession(Activity activity, final NetworkCallback<SocialAccount> callback) {

        callbacks.add(callback);

        openActiveSession(activity, true, getCallback(), Arrays.asList("email"));
    }

    public Session openSessionNoLogin(Activity activity) {
        return openActiveSession(activity, false, getCallback(), Arrays.asList("email", "publish_actions"));
    }

    public void share(final Prediction prediction, final Activity activity, final NetworkCallback<BaseModel> callback) {
        reauthorizeWithPublishPermissions(activity, new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                networkingManager.sharePredictionOnFacebook(prediction, callback);
            }
        });
    }

    public void reauthorizeWithPublishPermissions(final Activity activity, final NetworkCallback<SocialAccount> callback) {

        if (Session.getActiveSession() == null) {
            openSession(activity, new NetworkCallback<SocialAccount>() {
                @Override
                public void completionHandler(SocialAccount object, ServerError error) {
                    reauthorizeWithPublishPermissions(activity, callback);
                }
            });

            return;
        }

        if (Session.getActiveSession().getPermissions().contains("publish_actions")) {
            callback.completionHandler(userManager.getUser().getFacebookAccount(), null);
            return;
        }

        callbacks.add(new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                object.id = userManager.getUser().getFacebookAccount().id;
                userManager.updateSocialAccount(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        if (error != null)
                            callback.completionHandler(null, error);
                        else
                            callback.completionHandler(userManager.getUser().getFacebookAccount(), null);
                    }
                });
            }
        });

        Session.NewPermissionsRequest req = new Session.NewPermissionsRequest(activity, Arrays.asList("publish_actions"));
        req.setDefaultAudience(SessionDefaultAudience.FRIENDS);
        try {
            Session.getActiveSession().requestNewPublishPermissions(req);
        } catch (Exception e) {
            callback.completionHandler(null, null);
            Toast.makeText(activity, "Facebook connection failed", Toast.LENGTH_SHORT).show();
        }

    }

    private Session.StatusCallback getCallback() {
        return new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                Logger.log("FACEBOOK SESSION STATE CHANGED");
                if (exception != null) {
                    Session.getActiveSession().closeAndClearTokenInformation();
                    finish(null, new ServerError(exception.getMessage()));
                }

                if (state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED) {
                    getUserProfile();
                }
            }
        };
    }

    private void getUserProfile() {
        Request.executeMeRequestAsync(Session.getActiveSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user == null) {
                    finish(null, new ServerError(response.getError().getErrorMessage()));
                } else {
                    finish(buildSocialAccount(user), null);
                }
            }
        });
    }

    private void finish(SocialAccount account, ServerError error) {
        for (NetworkCallback<SocialAccount> callback : callbacks) {
            callback.completionHandler(account, error);
        }
        callbacks.clear();
    }

    private SocialAccount buildSocialAccount(GraphUser user) {
        SocialAccount account = new SocialAccount();
        account.providerName = "facebook";
        account.providerId = user.getId();
        account.accessToken = Session.getActiveSession().getAccessToken();

        return account;
    }

    public void postOnWall(final String msg, final Activity activity) {

        Session.OpenRequest openRequest = new Session.OpenRequest(activity).setPermissions(Arrays.asList("publish_actions")).setCallback(new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {

                if (session.isOpened()) {
                    Bundle params = new Bundle();
                    // params.putString("name", title);
                    // params.putString("caption", caption);
                    //params.putString("description", description);
                    //params.putString("link", link);
                    //params.putString("picture", pictureUrl);

                    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
                            activity, Session.getActiveSession(), params))
                            .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                                @Override
                                public void onComplete(Bundle values,
                                                       FacebookException error) {
                                    if (error == null) {
                                        // When the story is posted, echo the
                                        // success
                                        // and the post Id.
                                        final String postId = values
                                                .getString("post_id");
                                        if (postId != null) {
                                            System.out.println("Posted");
                                        } else {
                                            // User clicked the Cancel button
                                            System.out.println("Publish cancelled");
                                        }
                                    } else if (error instanceof FacebookOperationCanceledException) {
                                        // User clicked the "x" button
                                        System.out.println("Publish cancelled");
                                    } else {
                                        // Generic, ex: network error
                                        System.out.println("Error posting story");
                                    }
                                }

                            }).build();
                    feedDialog.show();
                }


            }
        });
        Session session = new Session.Builder(activity).build();
        Session.setActiveSession(session);
        session.openForPublish(openRequest);

    }

}

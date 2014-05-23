package managers;

import android.app.Activity;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.ServerError;
import models.SocialAccount;
import networking.NetworkCallback;

/**
 * Created by nick on 5/11/14.
 */
public class FacebookManager {

    private static ArrayList<NetworkCallback<SocialAccount>> callbacks = new ArrayList<NetworkCallback<SocialAccount>>();


    public void openSession(Activity activity, final NetworkCallback<SocialAccount> callback) {

        callbacks.add(callback);

        openActiveSession(activity, true, getCallback(), Arrays.asList("email", "public_profile"));
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

        callbacks.add(callback);

        if (Session.getActiveSession().getPermissions().contains("publish_actions")) {
            getUserProfile();
            return;
        }

        Session.NewPermissionsRequest req = new Session.NewPermissionsRequest(activity, Arrays.asList("publish_actions"));
        req.setDefaultAudience(SessionDefaultAudience.FRIENDS);
        Session.getActiveSession().requestNewPublishPermissions(req);
    }

    public boolean hasPublishPermissions() {
        return false;
    }

    private Session.StatusCallback getCallback() {
        return new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (exception != null) {
                    Session.getActiveSession().closeAndClearTokenInformation();
                    finish(null, new ServerError(exception.getMessage()));
                }

                if (state == SessionState.OPENED) {
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


}

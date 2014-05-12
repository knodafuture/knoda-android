package managers;

import android.app.Activity;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.util.ArrayList;

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

        Session.openActiveSession(activity, true, getCallback());
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


}

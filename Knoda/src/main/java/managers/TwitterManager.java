package managers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import models.ServerError;
import models.SocialAccount;
import networking.NetworkCallback;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import unsorted.Logger;

/**
 * Created by nick on 5/11/14.
 */
public class TwitterManager {

    private static final String twitterConsumerKey = "14fSb3CT7EEQkoryO8RNx7BrG";
    private static final String twitterConsumerSecret = "6Z5OGzxLL9NqVEpAbLs9FFd2PyLm6pd7j5r98IZr5e0HRr73bo";
    private static final String callbackURL = "knodaoauth://knoda";
    private static final String IEXTRA_AUTH_URL = "auth_url";
    private static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
    private static final String IEXTRA_OAUTH_TOKEN = "oauth_token";
    private static RequestToken token;
    private static AccessToken accessToken;
    private static Intent savedData;

    public void openSession(Activity activity) {
        new GetOauthTask().execute(activity);
    }

    public boolean hasAuthInfo() {
        return token != null;
    }

    private Twitter getTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(twitterConsumerKey);
        builder.setOAuthConsumerSecret(twitterConsumerSecret);
        Configuration config = builder.build();

        return new TwitterFactory(config).getInstance();
    }

    public void checkIntentData(Intent data) {
        savedData = data;
    }
    public void getSocialAccount(final NetworkCallback<SocialAccount> callback) {
        if (savedData != null) {
            Uri uri = savedData.getData();
            if (uri != null && uri.toString().startsWith(callbackURL)) {
                AccessTokenContainer container = new AccessTokenContainer();
                container.callback = callback;
                container.uri = uri;
                new GetAccessTokenTask().execute(container);
            } else {
                token = null;
                callback.completionHandler(null, new ServerError("Error authorizing with Twitter."));
            }
        } else {
            token = null;
            callback.completionHandler(null, new ServerError("Error authorizing with Twitter."));
        }
    }

    private SocialAccount getAccount() {
        if (accessToken == null)
            return null;

        SocialAccount account = new SocialAccount();
        account.providerName = "twitter";
        account.providerId = Long.toString(accessToken.getUserId());
        account.accessToken = accessToken.getToken();
        account.accessTokenSecret = accessToken.getTokenSecret();

        return account;

    }

    private class GetOauthTask extends AsyncTask<Activity, Void, Void> {
        @Override
        protected Void doInBackground(Activity... params) {
            Twitter twitter = getTwitter();
            try {
                token = twitter.getOAuthRequestToken(callbackURL);
                Activity activity = params[0];
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(token.getAuthenticationURL())));
                activity.finish();
            }
            catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Logger.log("ON POST EXECUTE");
        }
    }

    private class AccessTokenContainer {
        public NetworkCallback<SocialAccount> callback;
        public Uri uri;
    }
    private class GetAccessTokenTask extends AsyncTask<AccessTokenContainer, Void, AccessTokenContainer> {
        @Override
        protected AccessTokenContainer doInBackground(AccessTokenContainer...params) {
            AccessTokenContainer result = params[0];
            String verifier = result.uri.getQueryParameter(IEXTRA_OAUTH_VERIFIER);
            try {
                Twitter twitter = getTwitter();
                accessToken = twitter.getOAuthAccessToken(token, verifier);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(AccessTokenContainer result) {
            SocialAccount account = getAccount();
            if (account == null)
                result.callback.completionHandler(null, new ServerError("Error authorizing with Twitter."));
            else
                result.callback.completionHandler(account, null);
        }
    }

}

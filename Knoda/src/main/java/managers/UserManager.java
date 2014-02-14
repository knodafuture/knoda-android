package managers;

import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

import unsorted.Logger;
import networking.NetworkCallback;
import models.LoginRequest;
import models.LoginResponse;
import models.ServerError;
import models.SignUpRequest;
import models.User;

/**
 * Created by nick on 1/21/14.
 */
@Singleton
public class UserManager {

    public User user;

    @Inject NetworkingManager networkingManager;
    @Inject SharedPrefManager sharedPrefManager;

    public void refreshUser(final NetworkCallback<User> callback) {
        networkingManager.getCurrentUser(new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                if (error == null) {
                    user = object;
                } else {
                    Logger.log("Error getting user" + error.statusCode);
                }

                callback.completionHandler(user, error);
            }
        });
    }

    public void updateUser(final User u, final NetworkCallback<User> callback) {
        networkingManager.updateUser(u, new NetworkCallback<User>() {
            @Override
            public void completionHandler(User u, ServerError error) {
                user = u;
                callback.completionHandler(user, error);
            }
        });
    }

    public void login(final LoginRequest request, final NetworkCallback<User> callback) {
        networkingManager.login(request, new NetworkCallback<LoginResponse>() {
            @Override
            public void completionHandler(final LoginResponse loginResponse, ServerError error) {
                if (error != null) {
                    callback.completionHandler(null, error);
                    return;
                }
                else {
                    Logger.log(new Gson().toJson(loginResponse));
                    sharedPrefManager.setSavedAuthtoken(loginResponse.authToken);
                    refreshUser(new NetworkCallback<User>() {
                        @Override
                        public void completionHandler(User user, ServerError error) {
                            if (error != null) {
                                callback.completionHandler(null, error);
                                return;
                            } else {
                                sharedPrefManager.saveLoginRequestAndResponse(request, loginResponse);
                                callback.completionHandler(getUser(), null);
                            }
                        }
                    });
                }
            }
        });
    }

    public void signup(final SignUpRequest request, final NetworkCallback<User> callback) {
        networkingManager.signup(request, new NetworkCallback<LoginResponse>() {
            @Override
            public void completionHandler(final LoginResponse loginResponse, ServerError error) {
                if (error != null) {
                    callback.completionHandler(null, error);
                    return;
                } else {
                    sharedPrefManager.setSavedAuthtoken(loginResponse.authToken);
                    refreshUser(new NetworkCallback<User>() {
                        @Override
                        public void completionHandler(User object, ServerError error) {
                            if (error != null) {
                                callback.completionHandler(null, error);
                                return;
                            } else {
                                sharedPrefManager.saveSignupRequestAndResponse(request, loginResponse);
                                callback.completionHandler(getUser(), null);
                            }
                        }
                    });
                }
            }
        });
    }

    public void signout(final NetworkCallback<User> callback) {
        Logger.log("signout");
        networkingManager.signout(new NetworkCallback<User>() {
            @Override
            public void completionHandler(User u, ServerError error) {
                Logger.log("Clear session");
                sharedPrefManager.clearSession();
                callback.completionHandler(user, error);
            }
        });
    }

    public User getUser() {
        return user;
    }
}

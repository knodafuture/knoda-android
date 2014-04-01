package managers;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.Group;
import models.LoginRequest;
import models.LoginResponse;
import models.ServerError;
import models.SignUpRequest;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import unsorted.Logger;

/**
 * Created by nick on 1/21/14.
 */
@Singleton
public class UserManager {

    public User user;
    public ArrayList<Group> groups;

    NetworkingManager networkingManager;
    SharedPrefManager sharedPrefManager;

    @Inject
    public UserManager(NetworkingManager networkingManager, SharedPrefManager sharedPrefManager) {
        this.sharedPrefManager = sharedPrefManager;
        this.networkingManager = networkingManager;
    }

    public void refreshUser(final NetworkCallback<User> callback) {
        networkingManager.getCurrentUser(new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                if (error == null) {
                    user = object;
                    networkingManager.getGroups(new NetworkListCallback<Group>() {
                        @Override
                        public void completionHandler(ArrayList<Group> object, ServerError error) {
                            if (error == null) {
                                groups = object;
                            }
                            callback.completionHandler(user, error);
                        }
                    });
                } else {
                    Logger.log("Error getting user" + error.statusCode);
                }
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
        networkingManager.signout(new NetworkCallback<User>() {
            @Override
            public void completionHandler(User u, ServerError error) {
                Logger.log("Clear session");
                user = null;
                sharedPrefManager.clearSession();
                callback.completionHandler(user, error);
            }
        });
    }

    public User getUser() {
        return user;
    }

    public boolean isLoggedIn() {
        return sharedPrefManager.getSavedAuthtoken() != null;
    }

    public Group getGroupById(Integer id) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).id.equals(id)) {
                return groups.get(i);
            }
        }
        return null;
    }
}

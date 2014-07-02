package managers;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.Group;
import models.LoginRequest;
import models.LoginResponse;
import models.ServerError;
import models.SignUpRequest;
import models.SocialAccount;
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

    public void loginSavedUser(final NetworkCallback<User> callback) {
        LoginRequest request = sharedPrefManager.getSavedLoginRequest();
        SocialAccount account = sharedPrefManager.getSavedSocialAccount();


        if (sharedPrefManager.guestMode() && sharedPrefManager.getSavedAuthtoken() != null) {
            refreshUser(callback);
        } else {
            if (request != null) {
                login(request, callback);
            } else if (account != null) {
                socialSignIn(account, callback);
            } else {
                callback.completionHandler(null, new ServerError("No saved account."));
            }
        }
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

    public void refreshGroups(final NetworkListCallback<Group> callback) {
        networkingManager.getGroups(new NetworkListCallback<Group>() {
            @Override
            public void completionHandler(ArrayList<Group> object, ServerError error) {
                if (error == null) {
                    groups = object;
                }
                callback.completionHandler(object, error);
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
                } else {
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

    public void socialSignIn(final SocialAccount request, final NetworkCallback<User> callback) {
        networkingManager.socialSignIn(request, new NetworkCallback<LoginResponse>() {
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
                                sharedPrefManager.saveSocialAccountAndResponse(request, loginResponse);
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

    public void loginAsGuest(final NetworkCallback<User> callback) {
        networkingManager.loginAsGuest(new NetworkCallback<LoginResponse>() {
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
                                sharedPrefManager.saveGuestCredentials(loginResponse);
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

    public void deleteSocialAccount(final SocialAccount socialAccount, final NetworkCallback<User> callback) {
        networkingManager.deleteSocialAccount(socialAccount, new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    callback.completionHandler(getUser(), error);
                    return;
                }
                refreshUser(callback);
            }
        });
    }

    public void addSocialAccount(final SocialAccount socialAccount, final NetworkCallback<User> callback) {
        networkingManager.createSocialAccount(socialAccount, new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    callback.completionHandler(getUser(), error);
                    return;
                }
                refreshUser(callback);
            }
        });
    }

    public void updateSocialAccount(final SocialAccount socialAccount, final NetworkCallback<User> callback) {
        networkingManager.updateSocialAccount(socialAccount, new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    callback.completionHandler(getUser(), error);
                    return;
                }
                refreshUser(callback);
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

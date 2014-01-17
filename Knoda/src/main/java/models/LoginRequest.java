package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 1/16/14.
 */
public class LoginRequest extends BaseModel {

    @SerializedName("user")
    LoginRequestUser user;

    public LoginRequest(String login, String password) {
        this.user = new LoginRequestUser(login, password);
    }


}


class LoginRequestUser extends BaseModel {

    @SerializedName("login")
    String login;

    @SerializedName("password")
    String password;

    LoginRequestUser(String login, String password) {
        this.login = login;
        this.password = password;
    }

}


package models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest extends BaseModel {

    @SerializedName("user")
    LoginRequestUser user;

    
    public String login;
    public String password;

    public LoginRequest(String login, String password) {
        this.user = new LoginRequestUser(login, password);
        this.login = login;
        this.password = password;
    }
}


class LoginRequestUser {
    @SerializedName("login")
    public String login;

    @SerializedName("password")
    public String password;

    public LoginRequestUser(String login, String password) {
        this.login = login;
        this.password = password;
    }

}

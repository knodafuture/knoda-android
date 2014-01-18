package models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest extends BaseModel {

    @SerializedName("login")
    public String login;

    @SerializedName("password")
    public String password;

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}


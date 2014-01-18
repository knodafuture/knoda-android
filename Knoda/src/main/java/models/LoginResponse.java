package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 1/16/14.
 */
public class LoginResponse extends BaseModel {

    @SerializedName("auth_token")
    public String authToken;

    @SerializedName("user_id")
    public Integer userId;

    @SerializedName("email")
    public String email;

}

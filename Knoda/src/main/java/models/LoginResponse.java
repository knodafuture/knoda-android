package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 1/16/14.
 */
public class LoginResponse extends BaseModel {

    @SerializedName("auth_token")
    String authToken;

    @SerializedName("user_id")
    Integer userId;

    @SerializedName("email")
    String email;

}

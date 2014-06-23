package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 5/11/14.
 */
public class SocialAccount extends BaseModel {

    public Integer id;
    @SerializedName("provider_id")
    public String providerId;

    @SerializedName("provider_name")
    public String providerName;

    @SerializedName("provider_account_name")
    public String providerAccountName;

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("access_token_secret")
    public String accessTokenSecret;

}

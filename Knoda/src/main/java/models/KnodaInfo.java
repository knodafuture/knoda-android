package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeffcailteux on 6/25/14.
 */
public class KnodaInfo extends BaseModel {

    @SerializedName("username")
    public String username;
    @SerializedName("user_id")
    public Integer user_id;
    @SerializedName("avatar_image")
    public RemoteImage avatar;

}

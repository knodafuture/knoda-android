package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeffcailteux on 6/25/14.
 */
public class ContestUser extends BaseModel {
    @SerializedName("rank")
    public int rank;
    @SerializedName("rankText")
    public String rankText;
    @SerializedName("user_id")
    public int userId;
    @SerializedName("avatar_image")
    public RemoteImage avatar;
    @SerializedName("username")
    public String username;
    @SerializedName("won")
    public int won;
    @SerializedName("lost")
    public int lost;
    @SerializedName("verified_account")
    public boolean verified;
}

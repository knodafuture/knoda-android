package models;

import com.google.gson.annotations.SerializedName;

public class Leader {
    public String username;
    public Integer rank;
    @SerializedName("avatar_image")
    public RemoteImage avatar;
    @SerializedName("verified_account")
    public boolean verifiedAccount;
    public Integer won;
    public Integer lost;
}

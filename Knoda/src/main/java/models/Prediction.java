package models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nick on 1/8/14.
 */
public class Prediction extends BaseModel {

    public Integer id;
    public String body;
    public boolean outcome;

//    @SerializedName("expires_at")
//    public Date expirationDate;
//
//    @SerializedName("created_at")
//    public Date creationDate;
//
//    @SerializedName("closed_at")
//    public Date closeDate;
//
//    @SerializedName("resolution_date")
//    public Date resolutionDate;

    @SerializedName("short_url")
    public String shortUrl;

    @SerializedName("agreed_count")
    public Integer agreedCount;

    @SerializedName("disagreed_count")
    public Integer disagreedCount;

    @SerializedName("user_id")
    public Integer userId;

    public String username;

    @SerializedName("user_avatar")
    public RemoteImage userAvatar;

    public boolean expired;
    public boolean settled;

    @SerializedName("verified_account")
    public boolean verifiedAccount;

    @SerializedName("is_ready_for_resolution")
    public boolean isReadyForResolution;

    public ArrayList<Topic> tags = new ArrayList<Topic>();

}

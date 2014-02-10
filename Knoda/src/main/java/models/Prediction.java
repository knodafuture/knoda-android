package models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;

import helpers.DateUtil;

/**
 * Created by nick on 1/8/14.
 */
public class Prediction extends BaseModel {

    public Integer id;
    public String body;
    public boolean outcome;

    @SerializedName("expires_at")
    public DateTime expirationDate;

    @SerializedName("created_at")
    public DateTime creationDate;

    @SerializedName("closed_at")
    public DateTime closeDate;

    @SerializedName("resolution_date")
    public DateTime resolutionDate;

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

    public ArrayList<String> tags = new ArrayList<String>();

    @SerializedName("my_challenge")
    public Challenge challenge;

    @SerializedName("my_points")
    public PredictionPoints points;

    public String getCreationString() {
        return "made " + DateUtil.getPeriodString(creationDate) + " ago";
    }

    public String getExpirationString() {
        String string = expired ? "closed" : "closes";
        string += " " + DateUtil.getPeriodString(expirationDate);
        string += expired ? " ago" : "";
        return string;
    }

    public String getAgreePercentString() {
        float agreePercent = (float)agreedCount / (float)(agreedCount + disagreedCount) * 100;
        return (int)agreePercent + "% agree";
    }

    public String getMetdataString() {
        return getExpirationString() + " | " + getCreationString() + " | " + getAgreePercentString() + " | ";
    }
}

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

    @SerializedName("comment_count")
    public Integer commentCount;

    @SerializedName("group_id")
    public Integer groupId;

    @SerializedName("group_name")
    public String groupName;

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

    public boolean canSetOutcome() {
        return challenge != null && (isFinished() || passed72HoursSinceExpiration());

    }

    public boolean isFinished() {
        return challenge.isOwn && resolutionDate.isBeforeNow();
    }

    public boolean passed72HoursSinceExpiration() {
        return expirationDate.minusHours(72).isAfterNow();
    }

    public String pointsString() {

        String string = "";

        if (points.basePoints > 0)
            string += "+" + points.basePoints + " Base \n";
        if (points.outcomePoints > 0)
            string += "+" + points.outcomePoints + " Outcome \n";
        if (points.marketSizePoints > 0)
            string += "+" + points.marketSizePoints +  "Market \n";
        if (points.predictionMarketPoints > 0)
            string += "+" + points.predictionMarketPoints + " " + marketSizeNameForPoints(points.predictionMarketPoints) + "\n";

        return string;

    }

    public Integer totalPoints() {
        if (points == null)
            return 0;

        return points.predictionMarketPoints + points.basePoints + points.outcomePoints + points.marketSizePoints;
    }


    private String marketSizeNameForPoints(Integer points) {
        switch (points) {
            case 0: return "Too Easy";
            case 10:
            case 20: return "Favorite";
            case 30:
            case 40: return "Underdog";
            case 50: return "Longshot";
            default: return "";
        }
    }

    public boolean hasGroup() {
        return this.groupId != null && this.groupId > 0;
    }
}

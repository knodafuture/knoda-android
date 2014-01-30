package models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;

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

    public ArrayList<Topic> tags = new ArrayList<Topic>();



    public String getCreationString() {
        return "made " + getPeriodString(creationDate) + " ago";
    }

    public String getExpirationString() {
        String string = expired ? "closed" : "closes";
        string += " " + getPeriodString(expirationDate);
        string += expired ? "ago" : "";
        return string;
    }

    public String getAgreePercentString() {
        float agreePercent = (float)agreedCount / (float)(agreedCount + disagreedCount) * 100;
        return (int)agreePercent + "% agree";
    }

    public String getMetdataString() {
        return getExpirationString() + " | " + getCreationString() + " | " + getAgreePercentString() + " | ";
    }


    private String getPeriodString(DateTime date) {
        DateTime now = new DateTime();

        Period period;
        if (date.isBeforeNow())
            period = new Period(date, now);
        else
            period = new Period(now, date);

        if (period.getMinutes() <= 0)
            return period.getSeconds() + "s";
        else if (period.getHours() <= 0)
            return period.getMinutes() + "m";
        else if (period.getDays() <= 0)
            return period.getHours() + "h";
        else if (period.getMonths() <= 0)
            return period.getDays() + "d";
        else if (period.getYears() <= 0)
            return period.getMonths() + "mo";
        else {
            return period.getYears() + period.getYears() > 1 ? "yrs" : "yr";
        }
    }

}

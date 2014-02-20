package models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import helpers.DateUtil;

/**
 * Created by nick on 2/14/14.
 */
public class Comment extends BaseModel {

    public Integer id;
    public String text;

    @SerializedName("user_id")
    public Integer userId;

    public String username;

    @SerializedName("user_avatar")
    public RemoteImage userAvatar;

    public Challenge challenge;

    @SerializedName("verified_account")
    public boolean verifiedAccount;

    @SerializedName("created_at")
    public DateTime creationDate;

    @SerializedName("prediction_id")
    public Integer predictionId;

    public String getCreationString() {
       return "Made " + DateUtil.getPeriodString(creationDate) + " ago";
    }
}

package models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import helpers.DateUtil;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityItem extends BaseModel {

    public Integer id;

    @SerializedName("prediction_id")
    public Integer predictionId;

    @SerializedName("activity_type")
    public ActivityItemType type;

    @SerializedName("user_id")
    public Integer userId;

    @SerializedName("created_at")
    public DateTime creationDate;

    @SerializedName("prediction_body")
    public String predictionBody;

    public String title;

    public boolean seen;


    public String getCreationString() {
        return "made " + DateUtil.getPeriodString(creationDate) + " ago";
    }
}
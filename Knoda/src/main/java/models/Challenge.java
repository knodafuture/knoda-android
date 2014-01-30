package models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by nick on 1/30/14.
 */
public class Challenge extends BaseModel {


    public Integer id;

    @SerializedName("user_id")
    public Integer userId;

    @SerializedName("prediction_id")
    public Integer predictionId;

    public boolean agree;

    @SerializedName("created_at")
    public DateTime creationDate;

    @SerializedName("is_own")
    public boolean isOwn;

    @SerializedName("is_right")
    public boolean isRight;

    @SerializedName("is_finished")
    public boolean isFinished;

    @SerializedName("bs")
    public boolean isBS;
}

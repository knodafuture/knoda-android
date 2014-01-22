package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 1/21/14.
 */
public class User extends BaseModel {


    public Integer id;
    public String username;
    public String email;

    @SerializedName("verified_account")
    public boolean verified;

    public Integer points;
    public Integer won;
    public Integer lost;

    @SerializedName("winning_percentage")
    public Float winningPercentage;

    public String streak;

    @SerializedName("total_predictions")
    public Integer totalPredictions;


    @SerializedName("avatar_image")
    public RemoteImage avatar;



}

package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 1/8/14.
 */
public class Rivalry extends BaseModel {

    @SerializedName("user_won")
    public int user_won;
    @SerializedName("opponent_won")
    public int opponent_won;

}

package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeffcailteux on 8/27/14.
 */
public class Follow extends BaseModel {
    @SerializedName("leader_id")
    public int leader_id;
    @SerializedName("id")
    public int id;
    @SerializedName("user_id")
    public int user_id;
}

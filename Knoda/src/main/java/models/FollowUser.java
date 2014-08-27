package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeffcailteux on 8/26/14.
 */
public class FollowUser extends BaseModel {
    @SerializedName("leader_id")
    public int leader_id;
}

package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 4/6/14.
 */
public class Member extends BaseModel {

    public MembershipType role;
    public Integer id;

    @SerializedName("user_id")
    public Integer userId;

    @SerializedName("group_id")
    public Integer groupId;

    public String username;
}

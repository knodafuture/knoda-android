package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 4/7/14.
 */
public class GroupInvitation extends BaseModel {

    @SerializedName("group_id")
    public Integer groupId;

    @SerializedName("recipient_user_id")
    public Integer userId;

    @SerializedName("recipient_email")
    public String email;

    @SerializedName("recipient_phone")
    public String phoneNumber;
}

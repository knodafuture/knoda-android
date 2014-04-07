package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 4/7/14.
 */
public class JoinGroupRequest extends BaseModel {

    public String code;

    @SerializedName("group_id")
    public Integer groupId;
}

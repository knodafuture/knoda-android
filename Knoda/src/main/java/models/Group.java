package models;

import com.google.gson.annotations.SerializedName;

public class Group extends BaseModel {
    public Integer id;
    public String name;
    public String description;
    @SerializedName("member_count")
    public Integer memberCount;
    @SerializedName("my_info")
    public Leader rank;
    public Integer owner;
    @SerializedName("share_url")
    public String shareUrl;
    @SerializedName("leader_info")
    public Leader leader;

    @SerializedName("avatar_image")
    public RemoteImage avatar;

}
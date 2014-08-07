package models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nick on 1/21/14.
 */
public class Contest extends BaseModel {

    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("created_at")
    public String created;
    @SerializedName("avatar_image")
    public RemoteImage avatar;
    @SerializedName("leader_info")
    public ContestLeaderInfo contestLeaderInfo;
    @SerializedName("my_info")
    public ContestMyInfo contestMyInfo;
    @SerializedName("participants")
    public int participants;
    @SerializedName("contest_stages")
    public ArrayList<ContestStage> contestStages;
    @SerializedName("detail_url")
    public String detail_url;
}

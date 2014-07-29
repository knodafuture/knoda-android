package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 1/21/14.
 */
public class ContestStage extends BaseModel {


    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("contest_id")
    public int contest_id;
    @SerializedName("sort_order")
    public int sort_order;

}

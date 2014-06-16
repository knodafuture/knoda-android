package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeffcailteux on 6/13/14.
 */
public class Setting extends BaseModel {
    @SerializedName("display_name")
    public String displayName;
    @SerializedName("description")
    public String description;
    @SerializedName("id")
    public int id;
    @SerializedName("active")
    public boolean active;
}

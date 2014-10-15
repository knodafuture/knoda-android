package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeffcailteux on 10/15/14.
 */
public class EmbedLocation extends BaseModel {
    @SerializedName("url")
    public String url;
    @SerializedName("domain")
    public String domain;
}

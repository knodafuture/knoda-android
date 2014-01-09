package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 1/8/14.
 */
public class Topic extends BaseModel {

    @SerializedName("id")
    Integer id;

    @SerializedName("name")
    String name;
}

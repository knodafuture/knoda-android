package models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nick on 1/8/14.
 */
public class Topics extends BaseModel{

    @SerializedName("topics")
    ArrayList<Topic> topics = new ArrayList<Topic>();

}

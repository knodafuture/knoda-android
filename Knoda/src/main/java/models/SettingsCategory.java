package models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by jeffcailteux on 6/13/14.
 */
public class SettingsCategory extends BaseModel {

    @SerializedName("name")
    public String name;

    @SerializedName("settings")
    public ArrayList<Setting> settings;


}

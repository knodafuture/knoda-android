package models;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;

/**
 * Created by jeff on 8/18/14.
 */
public class UserContact extends BaseModel {

    @SerializedName("contact_id")
    public String contact_id;
    @SerializedName("emails")
    public HashSet<String> emails = new HashSet<String>();
    @SerializedName("phones")
    public HashSet<String> phones = new HashSet<String>();
    @SerializedName("Knoda_info")
    public KnodaInfo knodaInfo = null;

}

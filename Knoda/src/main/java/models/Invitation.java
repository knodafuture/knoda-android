package models;

import com.google.gson.annotations.SerializedName;

public class Invitation extends BaseModel {
    public Integer id;
    @SerializedName("invitation_link")
    public String invitationLink;
    public Group group;
}

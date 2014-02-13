package models;

import com.google.gson.annotations.SerializedName;

public class PasswordChangeRequest extends BaseModel {

    @SerializedName("current_password")
    public String currentPassword;

    @SerializedName("new_password")
    public String newPassword;
}

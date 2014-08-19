package models;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by jeff on 8/18/14.
 */
public class UserContacts extends BaseModel {
    public Collection<UserContact> contacts;
}

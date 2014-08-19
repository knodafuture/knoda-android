package models;

import java.util.ArrayList;

/**
 * Created by nick on 4/1/14.
 */
public class Contact extends BaseModel {

    public String name;
    public ArrayList<String> emailAddress = new ArrayList<String>();
    public ArrayList<String> phoneNumbers = new ArrayList<String>();


    public ArrayList<String> getContactMethods() {
        ArrayList<String> result = new ArrayList<String>();

        for (String phone : phoneNumbers)
            result.add(phone);

        for (String email : emailAddress)
            result.add(email);

        return result;
    }

}

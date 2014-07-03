package models;

import java.util.Comparator;

/**
 * Created by nick on 4/7/14.
 */
public class InvitationHolder {
    public User user;
    public Contact contact;

    public String selectedPhoneNumber;
    public String selectedEmail;

    public static InvitationHolder withContact(Contact contact) {
        InvitationHolder holder = new InvitationHolder();
        holder.contact = contact;
        return holder;
    }

    public static InvitationHolder withUser(User user) {
        InvitationHolder holder = new InvitationHolder();
        holder.user = user;
        return holder;
    }

    public static Comparator<InvitationHolder> comparator() {
        return new Comparator<InvitationHolder>() {
            @Override
            public int compare(InvitationHolder resultHolder, InvitationHolder resultHolder2) {
                String name1, name2;
                if (resultHolder.user != null)
                    name1 = resultHolder.user.username;
                else
                    name1 = resultHolder.contact.name;

                if (resultHolder2.user != null)
                    name2 = resultHolder2.user.username;
                else
                    name2 = resultHolder2.contact.name;

                return name1.compareTo(name2);
            }
        };
    }

    public String getName() {
        if (user != null)
            return user.username;
        if (contact != null)
            return contact.name;

        return null;
    }

    public boolean isKnodaUser() {
        return user != null;
    }

    public String getMetadataString() {
        if (isKnodaUser())
            return null;

        if (selectedPhoneNumber != null)
            return selectedPhoneNumber;
        if (selectedEmail != null)
            return selectedEmail;

        String result = "";

        boolean first = true;
        for (String phoneNumber : contact.phoneNumbers) {
            result = first ? phoneNumber : result + ", " + phoneNumber;
            first = false;
        }

        for (String email : contact.emailAddress) {
            result = first ? email : result + ", " + email;
            first = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object obj2) {

        if (obj2 == null)
            return false;
        if (!(obj2 instanceof InvitationHolder))
            return false;

        InvitationHolder obj3 = (InvitationHolder) obj2;


        if (user != null && obj3.user != null && user.equals(obj3.user))
            return true;
        if (contact != null && obj3.contact != null & contact.equals(obj3.contact))
            return true;
        if (selectedPhoneNumber != null && obj3.selectedPhoneNumber != null && selectedPhoneNumber.equals(obj3.selectedPhoneNumber))
            return true;
        if (selectedEmail != null && obj3.selectedEmail != null && selectedEmail.equals(obj3.selectedEmail))
            return true;

        return false;
    }

}

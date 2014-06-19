package models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by nick on 1/21/14.
 */
public class User extends BaseModel {


    @SerializedName("user_id")
    public Integer userId;
    public Integer id;
    public String username;
    public String email;
    public DateTime created_at;

    @SerializedName("verified_account")
    public boolean verified;

    @SerializedName("guest_mode")
    public boolean guestMode;

    public Integer points;
    public Integer won;
    public Integer lost;

    @SerializedName("winning_percentage")
    public Float winningPercentage;

    public String streak;

    @SerializedName("total_predictions")
    public Integer totalPredictions;


    @SerializedName("avatar_image")
    public RemoteImage avatar;

    @SerializedName("social_accounts")
    public ArrayList<SocialAccount> socialAccounts;

    public SocialAccount getTwitterAccount() {
        for (SocialAccount account : socialAccounts) {
            if (account.providerName.equals("twitter"))
                return account;
        }

        return null;
    }

    public SocialAccount getFacebookAccount() {
        for (SocialAccount account : socialAccounts) {
            if (account.providerName.equals("facebook"))
                return account;
        }

        return null;
    }
}

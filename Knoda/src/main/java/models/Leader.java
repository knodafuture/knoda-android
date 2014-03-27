package models;

import com.google.gson.annotations.SerializedName;

public class Leader extends BaseModel {
    public String username;
    public Integer rank;
    @SerializedName("avatar_image")
    public RemoteImage avatar;
    @SerializedName("verified_account")
    public boolean verifiedAccount;
    public Integer won;
    public Integer lost;

    public String getWinPercentageString() {
        if ((won + lost) == 0) {
            return "0%";
        } else {
            float winPercent = (float) won / (float) (won + lost) * 100;
            return (int) winPercent + "%";
        }
    }
}

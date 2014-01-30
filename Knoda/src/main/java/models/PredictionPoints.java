package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 1/30/14.
 */
public class PredictionPoints extends BaseModel {

    @SerializedName("base_points")
    public Integer basePoints;

    @SerializedName("outcome_points")
    public Integer outcomePoints;

    @SerializedName("market_size_points")
    public Integer marketSizePoints;

    @SerializedName("prediction_market_points")
    public Integer predictionMarketPoints;
}

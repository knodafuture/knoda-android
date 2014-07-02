package pubsub;

import models.Prediction;

/**
 * Created by adamengland on 2/25/14.
 */
public class NewPredictionEvent {
    public Prediction prediction;

    public NewPredictionEvent(Prediction prediction) {
        this.prediction = prediction;
    }
}

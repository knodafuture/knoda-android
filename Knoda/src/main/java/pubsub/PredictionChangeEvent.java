package pubsub;

import models.Prediction;

/**
 * Created by nick on 5/19/14.
 */
public class PredictionChangeEvent {
    public Prediction prediction;
    public PredictionChangeEvent(Prediction prediction) {this.prediction = prediction;}
}

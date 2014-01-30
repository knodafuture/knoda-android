package Factories;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import models.Prediction;

/**
 * Created by nick on 1/27/14.
 */
public class TypeTokenFactory {

    public static TypeToken getPredictionListTypeToken() {
        return new TypeToken<ArrayList<Prediction>>(){};
    }

}

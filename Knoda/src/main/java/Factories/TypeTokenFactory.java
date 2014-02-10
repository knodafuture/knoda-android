package factories;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import models.ActivityItem;
import models.Prediction;
import models.Tag;

/**
 * Created by nick on 1/27/14.
 */
public class TypeTokenFactory {

    public static TypeToken getPredictionListTypeToken() {
        return new TypeToken<ArrayList<Prediction>>(){};
    }

    public static TypeToken getActivityItemTypeToken() {
        return new TypeToken<ArrayList<ActivityItem>>(){};
    }

    public static TypeToken getTopicListTypeToken() {
        return new TypeToken<ArrayList<Tag>>(){};
    }

}

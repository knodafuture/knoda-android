package factories;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import models.ActivityItem;
import models.Badge;
import models.Comment;
import models.Group;
import models.Leader;
import models.Prediction;
import models.Tag;
import models.User;

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

    public static TypeToken getUserListTypeToken() {
        return new TypeToken<ArrayList<User>>(){};
    }

    public static TypeToken getBadgeListTypeToken() {
        return new TypeToken<ArrayList<Badge>>(){};
    }

    public static TypeToken getCommentListTypeToken() {
        return new TypeToken<ArrayList<Comment>>(){};
    }

    public static TypeToken getGroupListTypeToken() { return new TypeToken<ArrayList<Group>>(){}; }

    public static TypeToken getLeaderListTypeToken() { return new TypeToken<ArrayList<Leader>>(){}; }
}

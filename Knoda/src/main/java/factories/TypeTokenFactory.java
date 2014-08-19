package factories;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import models.ActivityItem;
import models.Comment;
import models.Contest;
import models.ContestUser;
import models.Group;
import models.Leader;
import models.Member;
import models.Prediction;
import models.SettingsCategory;
import models.Tag;
import models.User;
import models.UserContact;

/**
 * Created by nick on 1/27/14.
 */
public class TypeTokenFactory {

    public static TypeToken getPredictionListTypeToken() {
        return new TypeToken<ArrayList<Prediction>>() {
        };
    }

    public static TypeToken getActivityItemTypeToken() {
        return new TypeToken<ArrayList<ActivityItem>>() {
        };
    }

    public static TypeToken getTopicListTypeToken() {
        return new TypeToken<ArrayList<Tag>>() {
        };
    }

    public static TypeToken getUserListTypeToken() {
        return new TypeToken<ArrayList<User>>() {
        };
    }

    public static TypeToken getCommentListTypeToken() {
        return new TypeToken<ArrayList<Comment>>() {
        };
    }

    public static TypeToken getGroupListTypeToken() {
        return new TypeToken<ArrayList<Group>>() {
        };
    }

    public static TypeToken getLeaderListTypeToken() {
        return new TypeToken<ArrayList<Leader>>() {
        };
    }

    public static TypeToken getMemberListTypeToken() {
        return new TypeToken<ArrayList<Member>>() {
        };
    }

    public static TypeToken getSettingsTypeToken() {
        return new TypeToken<ArrayList<SettingsCategory>>() {
        };
    }

    public static TypeToken getContestsTypeToken() {
        return new TypeToken<ArrayList<Contest>>() {
        };
    }

    public static TypeToken getContestUserTypeToken() {
        return new TypeToken<ArrayList<ContestUser>>() {
        };
    }
    public static TypeToken getUserContactTypeToken() {
        return new TypeToken<ArrayList<UserContact>>() {
        };
    }
}

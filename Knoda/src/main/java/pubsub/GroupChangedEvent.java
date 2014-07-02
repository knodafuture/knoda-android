package pubsub;

import models.Group;

/**
 * Created by nick on 4/8/14.
 */
public class GroupChangedEvent {
    public Group group;

    public GroupChangedEvent(Group group) {
        this.group = group;
    }

}

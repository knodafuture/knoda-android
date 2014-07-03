package pubsub;

import models.Group;

public class NewGroupEvent {
    public Group group;

    public NewGroupEvent(Group group) {
        this.group = group;
    }
}

package pubsub;

import models.Group;

public class ChangeGroupEvent {
    public Group group;
    public ChangeGroupEvent(Group group) {
        this.group = group;
    }
}

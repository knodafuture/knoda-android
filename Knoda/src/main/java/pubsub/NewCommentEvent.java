package pubsub;

import models.Comment;

/**
 * Created by adamengland on 3/5/14.
 */
public class NewCommentEvent {
    public Comment comment;

    public NewCommentEvent(Comment comment) {
        this.comment = comment;
    }
}

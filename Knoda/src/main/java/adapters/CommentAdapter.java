package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import helpers.AdapterHelper;
import models.Comment;
import pubsub.NewCommentEvent;
import views.details.CommentCell;

/**
 * Created by nick on 2/17/14.
 */
public class CommentAdapter extends DetailsAdapter<Comment> {

    public Bus bus;
    private CommentAdapterDelegate delegate;

    public CommentAdapter(Context context, PagingAdapterDatasource<Comment> datasource, CommentAdapterDelegate delegate, ImageLoader imageLoader, Bus bus) {
        super(context, datasource, imageLoader);
        this.delegate = delegate;
        this.bus = bus;
        this.bus.register(this);
    }

    @Subscribe
    public void newComment(NewCommentEvent event) {
        insertAt(event.comment, objects.size());
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        position = transformPosition(position);

        if (position >= objects.size() || position < 0)
            return super.getView(position, convertView, parent);

        CommentCell listItem = (CommentCell) AdapterHelper.getConvertViewSafely(convertView, CommentCell.class);

        if (listItem == null)
            listItem = new CommentCell(context);

        final Comment comment = getItem(position);

        listItem.populate(comment);

        listItem.topContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onUserClicked(comment.userId);
            }
        });

        if (comment.userAvatar != null)
            listItem.avatarImage.setImageUrl(comment.userAvatar.small, imageLoader);

        return listItem;
    }

    public interface CommentAdapterDelegate {
        void onUserClicked(Integer userId);
    }
}

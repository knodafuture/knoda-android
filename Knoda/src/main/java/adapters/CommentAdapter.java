package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;

import helpers.AdapterHelper;
import models.Comment;
import views.details.CommentCell;

/**
 * Created by nick on 2/17/14.
 */
public class CommentAdapter extends DetailsAdapter<Comment> {

    private CommentAdapterDelegate delegate;

    public interface CommentAdapterDelegate {
        void onUserClicked(Integer userId);
    }

    public CommentAdapter(LayoutInflater inflater, PagingAdapterDatasource<Comment> datasource, CommentAdapterDelegate delegate, ImageLoader imageLoader) {
        super(inflater, datasource, imageLoader);
        this.delegate = delegate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        position = transformPosition(position);

        if (position >= objects.size() || position < 0)
            return super.getView(position, convertView, parent);

        CommentCell listItem = (CommentCell) AdapterHelper.getConvertViewSafely(convertView, CommentCell.class);

        if (listItem == null)
            listItem = new CommentCell(inflater.getContext());

        final Comment comment = getItem(position);

        listItem.usernameTextView.setText(comment.username);
        listItem.bodyTextView.setText(comment.text);
        listItem.timestampTextView.setText(comment.getCreationString());

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

}

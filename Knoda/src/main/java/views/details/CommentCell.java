package views.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

/**
 * Created by nick on 2/17/14.
 */
public class CommentCell extends RelativeLayout {


    public RelativeLayout topContainer;

    public NetworkImageView avatarImage;
    public TextView usernameTextView;
    public TextView bodyTextView;
    public TextView timestampTextView;

    public CommentCell(Context context) {
        super(context);
        initView(context);
    }

    public CommentCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_cell_comment, this);

        topContainer = (RelativeLayout)findViewById(R.id.comment_cell_top_container);
        avatarImage = (NetworkImageView)findViewById(R.id.comment_cell_avatar_imageview);
        usernameTextView = (TextView)findViewById(R.id.comment_cell_username_textview);
        bodyTextView = (TextView)findViewById(R.id.comment_cell_body_textview);
        timestampTextView = (TextView)findViewById(R.id.comment_cell_timestamps_textview);
    }
}

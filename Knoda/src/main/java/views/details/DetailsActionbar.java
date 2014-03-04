package views.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nick on 2/13/14.
 */
public class DetailsActionbar extends LinearLayout {

    public interface DetailsActionBarDelegate {
        void onComments();
        void onTally();
        void onSimilar();
        void onShare();
    }

    private DetailsActionBarDelegate delegate;

    public ImageView commentImageView;
    public TextView commentTextView;

    public ImageView tallyImageView;
    public TextView tallyTextView;

    public ImageView similarImageView;
    public ImageView shareImageView;

    public DetailsActionbar(Context context) {
        super(context);
        initView(context);
    }

    public DetailsActionbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DetailsActionbar(Context context, DetailsActionBarDelegate delegate) {
        super(context);
        initView(context);
        this.delegate = delegate;
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_details_actionbar, this);
        ButterKnife.inject(this);

        commentImageView = (ImageView)findViewById(R.id.details_action_comment_imageview);
        commentTextView = (TextView)findViewById(R.id.details_action_comment_textview);

        tallyImageView = (ImageView)findViewById(R.id.details_action_tally_imageview);
        tallyTextView = (TextView)findViewById(R.id.details_action_tally_textview);

        similarImageView = (ImageView)findViewById(R.id.details_action_similar_imageview);
        shareImageView = (ImageView)findViewById(R.id.details_action_share_imageview);

        setBackgroundColor(getResources().getColor(R.color.lightGray));
    }

    @OnClick(R.id.details_action_comment_clickable) void onComment() {
        commentTextView.setTextColor(getResources().getColor(R.color.knodaDarkGreen));
        commentImageView.setImageResource(R.drawable.action_commenticon_active);
        tallyImageView.setImageResource(R.drawable.action_tallyicon);
        tallyTextView.setTextColor(getResources().getColor(R.color.darkGray));
        delegate.onComments();
    }

    @OnClick(R.id.details_action_share_clickable) void onShare() {
        delegate.onShare();
    }

    @OnClick(R.id.details_action_tally_clickable) void onTally() {
        commentTextView.setTextColor(getResources().getColor(R.color.darkGray));
        commentImageView.setImageResource(R.drawable.action_commenticon);
        tallyImageView.setImageResource(R.drawable.action_tallyicon_active);
        tallyTextView.setTextColor(getResources().getColor(R.color.knodaDarkGreen));
        delegate.onTally();
    }

    @OnClick(R.id.details_action_similar_clickable) void onSimilar() {
        delegate.onSimilar();
    }
}

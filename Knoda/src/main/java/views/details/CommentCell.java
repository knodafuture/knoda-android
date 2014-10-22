package views.details;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import java.util.regex.Pattern;

import models.Comment;

/**
 * Created by nick on 2/17/14.
 */
public class CommentCell extends RelativeLayout {


    public RelativeLayout topContainer;

    public NetworkImageView avatarImage;
    public TextView usernameTextView;
    public TextView bodyTextView;
    public TextView timestampTextView;
    public ImageView verifiedCheckmark;
    public ImageView voteImage;

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

        topContainer = (RelativeLayout) findViewById(R.id.comment_cell_top_container);
        avatarImage = (NetworkImageView) findViewById(R.id.comment_cell_avatar_imageview);
        usernameTextView = (TextView) findViewById(R.id.comment_cell_username_textview);
        bodyTextView = (TextView) findViewById(R.id.comment_cell_body_textview);
        timestampTextView = (TextView) findViewById(R.id.comment_cell_timestamps_textview);
        verifiedCheckmark = (ImageView) findViewById(R.id.comment_cell_verified_checkmark);
        voteImage = (ImageView) findViewById(R.id.comment_cell_vote_image);
    }

    public void populate(Comment comment) {
        usernameTextView.setText(comment.username);
        timestampTextView.setText(comment.getCreationString());

        SpannableString spannableString =
                new SpannableString(comment.text);
        bodyTextView.setText(spannableString);

        Pattern hashtagPattern = Pattern.compile("[#]+[A-Za-z0-9-_]+\\b");
        String hashtagScheme = "content://com.knoda.knoda.hashtag/";
        Linkify.addLinks(bodyTextView, hashtagPattern, hashtagScheme);


        Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");
        String mentionScheme = "content://com.knoda.knoda.hashtag/";
        Linkify.addLinks(bodyTextView, mentionPattern, mentionScheme);

        Pattern webPattern = Patterns.WEB_URL;
        String webScheme = "content://com.knoda.knoda.hashtag/";
        Linkify.addLinks(bodyTextView, webPattern, webScheme);

        stripUnderlines(bodyTextView);

        if (comment.verifiedAccount)
            verifiedCheckmark.setVisibility(VISIBLE);
        else
            verifiedCheckmark.setVisibility(INVISIBLE);

        voteImage.setImageResource(getVoteImage(comment));
    }

    private int getVoteImage(Comment comment) {
        if (comment.challenge == null)
            return 0;

        if (comment.challenge.agree)
            return R.drawable.agree_marker;
        else if (!comment.challenge.agree)
            return R.drawable.disagree_marker;

        return 0;
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL(), getContext());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private class URLSpanNoUnderline extends URLSpan {
        Context context;

        public URLSpanNoUnderline(String url, Context c) {
            super(url);
            context = c;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            if (getURL().contains("@")) {
                ds.setColor(context.getResources().getColor(R.color.knodaLightGreen));
                ds.setFakeBoldText(true);
            } else if (getURL().contains(("#"))) {
                ds.setFakeBoldText(true);
                ds.setColor(context.getResources().getColor(R.color.knodaLightGreen));
            } else {
                ds.setFakeBoldText(true);
                ds.setColor(context.getResources().getColor(R.color.knodaLightGreen));
            }
        }
    }
}

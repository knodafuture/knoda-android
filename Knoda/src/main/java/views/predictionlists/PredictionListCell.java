package views.predictionlists;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.Prediction;

/**
 * Created by nick on 1/27/14.
 */
public class PredictionListCell extends RelativeLayout {

    public TextView usernameTextView;
    public TextView bodyTextView;
    public NetworkImageView avatarImageView;
    public TextView timeStampsTextView;
    public ImageView voteImageView;

    public RelativeLayout usernameView;
    public RelativeLayout bodyView;

    public TextView commentCountTextView;

    public Prediction prediction;

    public PredictionListCell(Context context) {
        super(context);
        initView(context);
    }

    public PredictionListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_cell_predictions, this);
        usernameTextView = (TextView)findViewById(R.id.prediction_cell_username_textview);
        bodyTextView = (TextView)findViewById(R.id.prediction_cell_body_textview);
        avatarImageView = (NetworkImageView)findViewById(R.id.prediction_cell_avatar_imageview);
        timeStampsTextView = (TextView)findViewById(R.id.prediction_cell_timestamps_textview);
        bodyView = (RelativeLayout)findViewById(R.id.prediction_cell_body_view);
        voteImageView = (ImageView)findViewById(R.id.prediction_cell_vote_image);
        usernameView = (RelativeLayout)findViewById(R.id.prediction_cell_top_container);
        commentCountTextView = (TextView)findViewById(R.id.prediction_cell_comment_textview);
    }



    public void setAgree(boolean agree) {
        int resId = agree? R.drawable.agree_marker : R.drawable.disagree_marker;
        voteImageView.setImageResource(resId);
    }

    public void setPrediction(Prediction prediction) {
        this.prediction = prediction;
        update();
    }


    public void update() {
        bodyTextView.setText(prediction.body);
        usernameTextView.setText(prediction.username);
        timeStampsTextView.setText(prediction.getMetdataString());

        commentCountTextView.setText(prediction.commentCount.toString());
        if (prediction.challenge != null)
            setAgree(prediction.challenge.agree);
    }

}
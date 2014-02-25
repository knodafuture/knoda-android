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
    public TextView resultTextView;
    public Prediction prediction;

    public RelativeLayout agreeView;
    public ImageView verifiedCheckmark;

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
        resultTextView = (TextView)findViewById(R.id.prediction_cell_result_textview);
        agreeView = (RelativeLayout)findViewById(R.id.prediction_cell_agreeview);
        verifiedCheckmark = (ImageView)findViewById(R.id.prediction_cell_verified_checkmark);
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


        if (prediction.verifiedAccount)
            verifiedCheckmark.setVisibility(VISIBLE);
        else
            verifiedCheckmark.setVisibility(INVISIBLE);

        updateVoteImage();
    }


    private void updateVoteImage() {

        if (prediction.isReadyForResolution && (prediction.challenge != null && prediction.challenge.isOwn) && !prediction.settled)
            voteImageView.setImageResource(R.drawable.prediction_alert);
        else
            voteImageView.setImageResource(getVoteImage());

        if (prediction.challenge == null || !prediction.settled) {
            resultTextView.setText("");
            return;
        }

        if (prediction.challenge.agree && prediction.outcome) {
            resultTextView.setText("W");
            resultTextView.setTextColor(getResources().getColor(R.color.knodaLightGreen));
        } else {
            resultTextView.setText("L");
            resultTextView.setTextColor(getResources().getColor(R.color.red));

        }
    }

    private int getVoteImage() {
        if (prediction.challenge == null)
            return 0;

        if (prediction.challenge.agree)
            return R.drawable.agree_marker;
        else if (!prediction.challenge.agree)
            return R.drawable.disagree_marker;

        return 0;
    }

}
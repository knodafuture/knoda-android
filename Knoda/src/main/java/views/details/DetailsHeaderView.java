package views.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import models.Prediction;
import views.core.MainActivity;
import views.predictionlists.PredictionListCell;

/**
 * Created by nick on 2/13/14.
 */
public class DetailsHeaderView extends RelativeLayout {

    public PredictionListCell predictionCell;
    public RelativeLayout actionsContainer;
    public RelativeLayout agreeDisagreeView;
    public Button agreeButton;
    public Button disagreeButton;
    public RelativeLayout settleView;
    public LinearLayout pointsView;
    public ImageView resultImageView;
    public TextView resultTextView;
    public TextView pointsTotalTextView;
    public TextView pointsDetailsTextView;
    public Prediction prediction;
    private DetailsHeaderViewDelegate delegate;
    MainActivity mainActivity;

    public DetailsHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public DetailsHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DetailsHeaderView(Context context, DetailsHeaderViewDelegate delegate, MainActivity mainActivity) {
        super(context);
        initView(context);
        this.delegate = delegate;
        this.mainActivity = mainActivity;
    }

    @OnClick(R.id.details_cell_agree_button)
    void onA() {
        delegate.onAgree();
    }

    @OnClick(R.id.details_cell_disagree_button)
    void onD() {
        delegate.onDisagree();
    }

    @OnClick(R.id.details_cell_bs_button)
    void onB() {
        delegate.onBS();
    }

    @OnClick(R.id.details_cell_yes_button)
    void onY() {
        delegate.onOutcome(true);
    }

    @OnClick(R.id.details_cell_no_button)
    void onN() {
        delegate.onOutcome(false);
    }

    @OnClick(R.id.details_cell_idk_button)
    void onI() {
        delegate.onIDK();
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_details_header, this);
        ButterKnife.inject(this);

        agreeDisagreeView = (RelativeLayout) findViewById(R.id.details_cell_agree_disagree);
        agreeButton = (Button) findViewById(R.id.details_cell_agree_button);
        disagreeButton = (Button) findViewById(R.id.details_cell_disagree_button);

        settleView = (RelativeLayout) findViewById(R.id.details_cell_settle);

        pointsView = (LinearLayout) findViewById(R.id.details_cell_points);
        resultImageView = (ImageView) findViewById(R.id.results_outcome_imageview);
        resultTextView = (TextView) findViewById(R.id.results_outcome_textview);
        pointsTotalTextView = (TextView) findViewById(R.id.results_points_textview);
        pointsDetailsTextView = (TextView) findViewById(R.id.details_cell_points_breakdown_textview);

        predictionCell = (PredictionListCell) findViewById(R.id.details_cell_prediction_cell);
        actionsContainer = (RelativeLayout) findViewById(R.id.details_cell_actions_container);
    }

    public void setPrediction(Prediction prediction) {
        this.prediction = prediction;
        this.predictionCell.setPrediction(prediction, mainActivity);
        update();
    }

    public void update() {
        configureVariableSpot();
    }

    private void configureVariableSpot() {
        if (prediction.closeDate != null && prediction.challenge != null)
            updateAndShowPoints();
        else if (prediction.canSetOutcome() && prediction.challenge.isOwn)
            updateAndShowSettle();
        else if (!prediction.expired && (prediction.challenge == null || !prediction.challenge.isOwn))
            updateAndShowAgreeDisagree();
        else
            actionsContainer.setVisibility(GONE);
    }

    private void updateAndShowAgreeDisagree() {
        agreeDisagreeView.setVisibility(VISIBLE);
        settleView.setVisibility(INVISIBLE);
        pointsView.setVisibility(INVISIBLE);

        int lightGreen = getResources().getColor(R.color.knodaLightGreen);
        int darkGreen = getResources().getColor(R.color.knodaDarkGreen);

        if (prediction.challenge == null) {
            agreeButton.setBackgroundColor(lightGreen);
            disagreeButton.setBackgroundColor(lightGreen);
        } else if (prediction.challenge.agree) {
            agreeButton.setBackgroundColor(darkGreen);
            disagreeButton.setBackgroundColor(lightGreen);
        } else {
            agreeButton.setBackgroundColor(lightGreen);
            disagreeButton.setBackgroundColor(darkGreen);
        }
    }

    private void updateAndShowSettle() {
        agreeDisagreeView.setVisibility(INVISIBLE);
        settleView.setVisibility(VISIBLE);
        pointsView.setVisibility(INVISIBLE);
    }

    private void updateAndShowPoints() {
        agreeDisagreeView.setVisibility(INVISIBLE);
        settleView.setVisibility(INVISIBLE);
        pointsView.setVisibility(VISIBLE);

        pointsDetailsTextView.setText(prediction.pointsString());
        pointsTotalTextView.setText(prediction.totalPoints().toString());

        boolean win;

        if (prediction.challenge == null)
            return;

        if (prediction.challenge.agree)
            win = prediction.outcome ? true : false;
        else
            win = prediction.outcome ? false : true;

        if (win) {
            resultTextView.setText("YOU WON!");
            resultImageView.setImageResource(R.drawable.result_win_icon);
        } else {
            resultTextView.setText("YOU LOSE!");
            resultImageView.setImageResource(R.drawable.result_lose_icon);
        }
    }

    public interface DetailsHeaderViewDelegate {
        void onAgree();

        void onDisagree();

        void onBS();

        void onIDK();

        void onOutcome(boolean outcome);
    }
}

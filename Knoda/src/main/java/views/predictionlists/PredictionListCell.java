package views.predictionlists;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

/**
 * Created by nick on 1/27/14.
 */
public class PredictionListCell extends RelativeLayout {

    public TextView usernameTextView;
    public TextView bodyTextView;

    public PredictionListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        usernameTextView = (TextView)findViewById(R.id.prediction_cell_username_textview);
        bodyTextView = (TextView)findViewById(R.id.prediction_cell_body_textview);
    }
}

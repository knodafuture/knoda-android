package views.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

/**
 * Created by nick on 2/18/14.
 */
public class TallyCell extends RelativeLayout {

    public TextView leftTextView;
    public ImageView leftCheckmark;
    public TextView rightTextView;
    public ImageView rightCheckmark;

    public TallyCell(Context context) {
        super(context);
        initView(context);
    }

    public TallyCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_cell_tally, this);
        leftTextView = (TextView) findViewById(R.id.tally_cell_left_textview);
        rightTextView = (TextView) findViewById(R.id.tally_cell_right_textview);
        rightCheckmark = (ImageView) findViewById(R.id.tally_cell_right_verified_checkmark);
        leftCheckmark = (ImageView) findViewById(R.id.tally_cell_left_verified_checkmark);
    }
}

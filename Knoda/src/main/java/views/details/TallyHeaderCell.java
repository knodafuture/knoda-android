package views.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

/**
 * Created by nick on 2/18/14.
 */
public class TallyHeaderCell extends RelativeLayout {

    public TextView agreedTextView;
    public TextView disagreedTextView;

    public TallyHeaderCell(Context context) {
        super(context);
        initView(context);
    }

    public TallyHeaderCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_cell_tally_header, this);
        agreedTextView = (TextView) findViewById(R.id.tally_header_agreed_textview);
        disagreedTextView = (TextView) findViewById(R.id.tally_header_disagreed_textview);
    }
}

package views.group;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.knoda.knoda.R;

public class GroupPredictionListHeader extends RelativeLayout {
    public GroupPredictionListHeader(Context context) {
        super(context);
        initView(context);
    }

    public GroupPredictionListHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_group_prediction_list_header, this);
    }
}

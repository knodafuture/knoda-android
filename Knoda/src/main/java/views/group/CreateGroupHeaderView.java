package views.group;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.knoda.knoda.R;

/**
 * Created by adamengland on 3/25/14.
 */
public class CreateGroupHeaderView extends RelativeLayout {
    public CreateGroupHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public CreateGroupHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_create_group_header, this);
    }
}

package views.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.knoda.knoda.R;

/**
 * Created by nick on 2/3/14.
 */
public class UserProfileHeaderView extends RelativeLayout {

    public UserProfileHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public UserProfileHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_another_user_header, this);
    }
}

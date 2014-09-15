package views.profile;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import adapters.AnotherProfilePagerAdapter;
import models.User;

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

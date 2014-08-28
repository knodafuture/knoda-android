package views.predictionlists;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

/**
 * Created by nick on 2/11/14.
 */
public class HomeActionBar extends RelativeLayout {

    private HomeActionBarCallbacks callbacks;
    public TextView viewAllTV;
    public TextView followingTV;

    public int selected = 0;

    public HomeActionBar(Context context) {
        super(context);
        initView(context);
    }

    public HomeActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HomeActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_home_actionbar, this);
        ((ImageView) findViewById(R.id.home_actionbar_addfriends)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onAddFriendsClick();
            }
        });
        ((ImageView) findViewById(R.id.home_actionbar_search)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onSearchClick();
            }
        });
        viewAllTV = ((TextView) findViewById(R.id.home_actionbar_viewall));
        followingTV = ((TextView) findViewById(R.id.home_actionbar_following));
        viewAllTV.setTextColor(context.getResources().getColor(R.color.knodaDarkGreen));
        followingTV.setTextColor(context.getResources().getColor(R.color.lightGray));

        viewAllTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected == 0)
                    return;
                selected = 0;
                viewAllTV.setTextColor(v.getContext().getResources().getColor(R.color.knodaDarkGreen));
                followingTV.setTextColor(v.getContext().getResources().getColor(R.color.lightGray));
                callbacks.onSwitchFeed(0);

            }
        });
        followingTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected == 1)
                    return;
                selected = 1;
                viewAllTV.setTextColor(v.getContext().getResources().getColor(R.color.lightGray));
                followingTV.setTextColor(v.getContext().getResources().getColor(R.color.knodaDarkGreen));
                callbacks.onSwitchFeed(1);

            }
        });
    }

    public void setCallbacks(HomeActionBarCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public interface HomeActionBarCallbacks {
        void onSearchClick();

        void onAddFriendsClick();
        void onSwitchFeed(int number);
    }

}

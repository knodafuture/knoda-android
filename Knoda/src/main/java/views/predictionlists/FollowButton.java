package views.predictionlists;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.knoda.knoda.R;

/**
 * Created by nick on 2/11/14.
 */
public class FollowButton extends RelativeLayout {

    private Button button;

    private FollowButtonCallbacks callbacks;

    public FollowButton(Context context) {
        super(context);
        initView(context);
    }

    public FollowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FollowButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_follow_button, this);
        button = (Button) findViewById(R.id.view_follow_button);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callbacks != null)
                    callbacks.onFollowClick();
            }
        });
    }

    public void setCallbacks(FollowButtonCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public interface FollowButtonCallbacks {
        void onFollowClick();
    }

}

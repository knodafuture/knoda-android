package views.profile;

import android.content.Context;
import android.graphics.Color;
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
public class MyProfileActionBar extends RelativeLayout {

    public TextView titleTV;
    private MyProfileActionBarCallbacks callbacks;

    public MyProfileActionBar(Context context) {
        super(context);
        initView(context);
    }

    public MyProfileActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyProfileActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_myprofile_actionbar, this);
        ((ImageView) findViewById(R.id.myprofile_actionbar_vs)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onVersusClick();
            }
        });
        ((ImageView) findViewById(R.id.myprofile_actionbar_settings)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onSettingsClick();
            }
        });
       titleTV = ((TextView) findViewById(R.id.myprofile_actionbar_title));

    }

    public void setCallbacks(MyProfileActionBarCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public interface MyProfileActionBarCallbacks {
        void onSettingsClick();
        void onVersusClick();
    }


}

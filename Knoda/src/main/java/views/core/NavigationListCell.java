package views.core;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

/**
 * Created by nick on 1/27/14.
 */
public class NavigationListCell extends RelativeLayout implements Checkable {

    private boolean isChecked;

    public ImageView iconImageView;
    public TextView labelTextView;
    public ImageView selectedBackgroundView;
    public TextView rightTextView;

    public NavigationListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        iconImageView = (ImageView) findViewById(R.id.navigation_list_icon);
        labelTextView = (TextView) findViewById(R.id.navigation_list_text);
        //selectedBackgroundView = (ImageView) findViewById(R.id.navigation_list_selected_background);
        rightTextView = (TextView)findViewById(R.id.navigation_list_right_textview);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;

//        if (isChecked)
//            selectedBackgroundView.setVisibility(VISIBLE);
//        else
//            selectedBackgroundView.setVisibility(INVISIBLE);
    }

    public void toggle() {
        setChecked(!isChecked);
    }

}

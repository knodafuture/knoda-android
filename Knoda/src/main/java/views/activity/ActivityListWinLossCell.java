package views.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.ActivityItem;
import models.ActivityItemType;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityListWinLossCell extends RelativeLayout {


    public NetworkImageView iconImageView;
    public TextView winlosstitle;
    public TextView winlosscomment;
    public Button winlossbutton;
    public RelativeLayout buttonContainer;
    public View divider;

    final static String bragbg = "#EBF5DE";
    final static String bragborder = "#77BC1F";

    final static String settlebg = "#FFE1E1";
    final static String settleborder = "#FE3232";

    final static String groupbg = "#DEE7E1";
    final static String groupborder = "#235C37";

    public ActivityItem activityItem;

    public ActivityListWinLossCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onFinishInflate() {
        iconImageView = (NetworkImageView) findViewById(R.id.winloss_imageview);
        winlosstitle = (TextView) findViewById(R.id.winloss_title);
        winlosscomment = (TextView) findViewById(R.id.winloss_comment);
        winlossbutton = (Button) findViewById(R.id.winloss_button);
        buttonContainer = (RelativeLayout) findViewById(R.id.winloss_button_container);
        divider = findViewById(R.id.divider);
    }
}

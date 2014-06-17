package views.activity;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import models.ActivityItem;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityListWinLossCell extends RelativeLayout {


    public ImageView iconImageView;
    public TextView winlosswinlosstext;
    public TextView winlosscomment;
    public Button winlossbutton;

    final static String bragbg="#EBF5DE";
    final static String bragborder="#77BC1F";

    final static String settlebg="#FFE1E1";
    final static String settleborder="#FE3232";

    final static String groupbg="#DEE7E1";
    final static String groupborder="#235C37 ";

    public ActivityItem activityItem;

    public ActivityListWinLossCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onFinishInflate() {
        iconImageView = (ImageView)findViewById(R.id.winloss_imageview);
        winlosswinlosstext= (TextView)findViewById(R.id.winloss_winloss_text);
        winlosscomment = (TextView)findViewById(R.id.winloss_comment);
        winlossbutton = (Button) findViewById(R.id.winloss_button);
    }

    public void setActivityItem(ActivityItem activityItem) {
        this.activityItem = activityItem;
        update();
    }

    public void update() {
        if(activityItem.type.equals("COMMENT")){
            hideButton();
            winlosswinlosstext.setText(Html.fromHtml(activityItem.text));

        }else if(activityItem.type.equals("WON")){
            activityItem.text="<font color='#77BC1F'>You Won</font>"+activityItem.text;
            winlossbutton.setTextColor(Color.parseColor(bragborder));
            winlossbutton.setBackgroundResource(R.drawable.brag_button);
            winlosswinlosstext.setText(Html.fromHtml(activityItem.text));

        }else if(activityItem.type.equals("LOST")){
            activityItem.text="<font color='#FE3232'>You Lost</font>"+activityItem.text;
            hideButton();
            winlosswinlosstext.setText(Html.fromHtml(activityItem.text));

        }else if(activityItem.type.equals("INVITATION")){
            winlossbutton.setTextColor(Color.parseColor(groupborder));
            winlossbutton.setBackgroundResource(R.drawable.group_button);
            winlosswinlosstext.setText(Html.fromHtml(activityItem.text));

        }else if(activityItem.type.equals("EXPIRED")){
            winlossbutton.setTextColor(Color.parseColor(settleborder));
            winlossbutton.setBackgroundResource(R.drawable.settle_button);
            winlosswinlosstext.setText(Html.fromHtml(activityItem.text));

        }
    }
    public void hideButton(){
        winlossbutton.setVisibility(INVISIBLE);
        ViewGroup.LayoutParams lp = winlossbutton.getLayoutParams();
        lp.height=0;
        winlossbutton.setLayoutParams(lp);
    }
}

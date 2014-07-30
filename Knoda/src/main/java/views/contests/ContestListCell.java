package views.contests;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.Contest;

/**
 * Created by nick on 2/3/14.
 */
public class ContestListCell extends RelativeLayout {

    public TextView titleTV;
    public TextView descriptionTV;
    public TextView leaderTV;
    public TextView placeTV;
    public TextView overallTV;
    private ColorStateList csl;
    public RelativeLayout buttonContainer;
    public TextView buttonText;
    public LinearLayout standingsContainer;

    public NetworkImageView avatarImageView;


    public ContestListCell(Context context) {
        super(context);
    }

    public ContestListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        titleTV = (TextView) findViewById(R.id.contest_title);
        descriptionTV = (TextView) findViewById(R.id.contest_description);
        leaderTV = (TextView) findViewById(R.id.contest_leader);
        placeTV = (TextView) findViewById(R.id.contest_place);
        overallTV = (TextView) findViewById(R.id.contest_overall);
        avatarImageView = (NetworkImageView) findViewById(R.id.contest_avatar);
        buttonContainer = (RelativeLayout) findViewById(R.id.contest_button_container);
        buttonText = (TextView) findViewById(R.id.contest_button);
        buttonText.setTextColor(getResources().getColorStateList(R.color.group_selector_text));
        standingsContainer = (LinearLayout) findViewById(R.id.contest_standings_container);
    }

    public void setHeaderMode() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) findViewById(R.id.spacer).getLayoutParams();
        lp.height = 1;
        findViewById(R.id.spacer).setLayoutParams(lp);
        descriptionTV.setMaxLines(1000);
        descriptionTV.setEllipsize(null);
    }


    public void setContest(Contest contest) {
        setTag(contest);
        titleTV.setText(contest.name);
        descriptionTV.setText(contest.description);
        if (contest.contestLeaderInfo != null)
            leaderTV.setText(contest.contestLeaderInfo.username);
        if (contest.contestMyInfo != null)
            placeTV.setText(getPlace(contest.contestMyInfo.rank));
        overallTV.setText("overall(" + contest.participants + ")");
        if (contest.contestMyInfo == null) {//explore
            buttonContainer.setVisibility(VISIBLE);
            standingsContainer.setVisibility(INVISIBLE);
        }

    }

    public String getPlace(int i) {
        if (i % 10 == 1 && i != 11)
            return i + "st";
        else if (i % 10 == 2 && i != 12)
            return i + "nd";
        else if (i % 10 == 3 && i != 13)
            return i + "rd";
        else
            return i + "th";
    }
}

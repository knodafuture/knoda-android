package views.contests;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
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

    public NetworkImageView avatarImageView;

    ImageLoader imageLoader;

    public ContestListCell(Context context, ImageLoader il) {
        super(context);
        imageLoader = il;
        initView(context);
    }

    public ContestListCell(Context context, AttributeSet attrs, ImageLoader il) {
        super(context, attrs);
        imageLoader = il;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_cell_contest, this);

        titleTV = (TextView) findViewById(R.id.contest_title);
        descriptionTV = (TextView) findViewById(R.id.contest_description);
        leaderTV = (TextView) findViewById(R.id.contest_leader);
        placeTV = (TextView) findViewById(R.id.contest_place);
        overallTV = (TextView) findViewById(R.id.contest_overall);
        avatarImageView = (NetworkImageView) findViewById(R.id.contest_avatar);

    }


    public void setContest(Contest contest) {
        titleTV.setText(contest.name);
        descriptionTV.setText(contest.description);
        avatarImageView.setImageUrl(contest.avatar, imageLoader);

        //leaderTV.setText(contest.leader);
        //placeTV.setText(getPlace(contest.rank));
        overallTV.setText("overall(" + contest.participants + ")");

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

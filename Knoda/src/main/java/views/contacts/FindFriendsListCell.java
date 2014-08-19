package views.contacts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import models.UserContact;

/**
 * Created by jeff on 7/31/2014.
 */
public class FindFriendsListCell extends RelativeLayout {

    final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
    AbsListView.LayoutParams lp_header = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, onedp * 20);
    AbsListView.LayoutParams lp_normal = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, onedp * 40);

    public TextView title;
    public TextView description;
    public CheckBox checkBox;
    public ImageView plusBtn;

    public FindFriendsListCell(Context context) {
        super(context);
    }

    public FindFriendsListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        title = (TextView) findViewById(R.id.findfriends_listcell_title);
        description = (TextView) findViewById(R.id.findfriends_listcell_description);
        checkBox = (CheckBox) findViewById(R.id.findfriends_listcell_check);
        plusBtn = (ImageView) findViewById(R.id.findfriends_listcell_btn);
    }

    public void setUser(UserContact userContact) {
        title.setText(userContact.contact_id);
        if (userContact.knodaInfo != null) {
            description.setText(userContact.knodaInfo.username);
            checkBox.setVisibility(VISIBLE);
            plusBtn.setVisibility(GONE);
        } else {
            checkBox.setVisibility(GONE);
            plusBtn.setVisibility(VISIBLE);
            if (userContact.phones.size() > 0) {
                int x = 0;
                for (String s : userContact.phones) {
                    if (x == 0) {
                        description.setText(s);
                        break;
                    }
                }
            } else {
                if (userContact.emails.size() > 0) {
                    int x = 0;
                    for (String s : userContact.emails) {
                        if (x == 0) {
                            description.setText(s);
                            break;
                        }
                    }
                    //description.setText(userContact.emails.get(0));
                }
            }

        }
    }
}

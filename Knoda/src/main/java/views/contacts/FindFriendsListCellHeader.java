package views.contacts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import java.util.ArrayList;

import adapters.UserContactAdapter;
import models.UserContact;

/**
 * Created by jeff on 7/31/2014.
 */
public class FindFriendsListCellHeader extends RelativeLayout {

    public static final int CONTACTS = 0;
    public static final int INVITE = 1;
    public static final int FACEBOOK = 2;
    public static final int TWITTER = 3;
    final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
    public TextView title;
    public TextView selectall;
    public CheckBox checkBox;

    public FindFriendsListCellHeader(Context context) {
        super(context);
    }

    public FindFriendsListCellHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        title = (TextView) findViewById(R.id.findfriendsheader_listcell_title);
        selectall = (TextView) findViewById(R.id.findfriendsheader_listcell_selectall);
        checkBox = (CheckBox) findViewById(R.id.findfriendsheader_listcell_check);
    }

    public void setMode(int i, final UserContactAdapter adapter) {
        //0=contacts
        //1=invite
        //2=facebook
        //3=twitter
        switch (i) {
            case 0:
                title.setText("Your Contacts on Knoda");
                selectall.setVisibility(VISIBLE);
                checkBox.setVisibility(VISIBLE);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        adapter.followAll(isChecked);
                    }
                });
                break;
            case 1:
                title.setText("Invite Contacts");
                selectall.setVisibility(INVISIBLE);
                checkBox.setVisibility(INVISIBLE);
                break;
            case 2:
                title.setText("Facebook Friends on Knoda");
                selectall.setVisibility(VISIBLE);
                checkBox.setVisibility(VISIBLE);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        adapter.followAll(isChecked);
                    }
                });
                break;
            case 3:
                title.setText("Twitter Followers on Knoda");
                selectall.setVisibility(VISIBLE);
                checkBox.setVisibility(VISIBLE);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        adapter.followAll(isChecked);
                    }
                });
                break;
        }

    }
}

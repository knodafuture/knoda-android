package views.contacts;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import java.util.HashMap;

import adapters.UserContactAdapter;
import models.GroupInvitation;
import models.KnodaInfo;
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
    public Button plusBtn;

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
        plusBtn = (Button) findViewById(R.id.findfriends_listcell_btn);
    }

    public void setUser(final UserContact userContact, UserContactAdapter adapter, final FindFriendsActivity parent) {
        title.setText(userContact.contact_id);
        final HashMap<String, KnodaInfo> followingSet;
        if (adapter.type == FindFriendsListCellHeader.FACEBOOK)
            followingSet = parent.followingFacebook;
        else if (adapter.type == FindFriendsListCellHeader.TWITTER)
            followingSet = parent.followingTwitter;
        else
            followingSet = parent.following;
        if (userContact.knodaInfo != null) {
            //follow
            description.setText(userContact.knodaInfo.username);
            checkBox.setVisibility(VISIBLE);
            plusBtn.setVisibility(GONE);
            checkBox.setChecked(followingSet.containsKey(userContact.contact_id));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        followingSet.put(userContact.contact_id, userContact.knodaInfo);
                    } else {
                        followingSet.remove(userContact.contact_id);
                    }
                    parent.setSubmitBtnText();
                }
            });
        } else {
            //invite
            checkBox.setVisibility(GONE);
            plusBtn.setVisibility(VISIBLE);
            String d = "";
            if (userContact.phones != null && userContact.phones.size() > 0) {
                for (String s : userContact.phones) {
                    String phone = "(" + s.substring(0, 3) + ") " + s.substring(3, 6) + "-" + s.substring(6);
                    d += phone + ", ";
                }
            }
            if (userContact.emails != null && userContact.emails.size() > 0) {
                for (String s : userContact.emails) {
                    d += s + ", ";
                }
            }
            description.setText(d.substring(0, d.length() - 2));
            if (parent.inviting.containsKey(userContact.contact_id))
                plusBtn.setBackgroundResource(R.drawable.ic_invite);
            else
                plusBtn.setBackgroundResource(R.drawable.ic_invite_active);
            plusBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (parent.inviting.containsKey(userContact.contact_id)) {
                        parent.inviting.remove(userContact.contact_id);
                        plusBtn.setBackgroundResource(R.drawable.ic_invite_active);
                        parent.setSubmitBtnText();
                    } else {
                        GroupInvitation groupInvitation = new GroupInvitation();
                        if (userContact.emails != null && userContact.emails.size() > 0)
                            groupInvitation.email = (String) userContact.emails.toArray()[0];
                        if (userContact.phones != null && userContact.phones.size() > 0)
                            groupInvitation.phoneNumber = (String) userContact.phones.toArray()[0];
                        parent.inviting.put(userContact.contact_id, groupInvitation);
                        plusBtn.setBackgroundResource(R.drawable.ic_invite);
                        parent.setSubmitBtnText();
                    }
                }
            });

        }
    }
}

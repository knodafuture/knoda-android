package views.contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import java.util.HashMap;
import java.util.HashSet;

import adapters.UserContactAdapter;
import models.GroupInvitation;
import models.KnodaInfo;
import models.UserContact;

/**
 * Created by jeff on 7/31/2014.
 */
public class FindFriendsListCell extends RelativeLayout {

    public TextView title;
    public TextView description;
    public CheckBox checkBox;
    public Button plusBtn;
    public NetworkImageView avatar;

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
        avatar = (NetworkImageView) findViewById(R.id.findfriends_listcell_avatar_imageview);
    }

    public void setUser(final UserContact userContact, final UserContactAdapter adapter, final FindFriendsActivity parent) {
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
            avatar.setVisibility(VISIBLE);
            findViewById(R.id.findfriends_listcell_avatar_container).setVisibility(VISIBLE);
            if (userContact.knodaInfo != null && userContact.knodaInfo.avatar != null && userContact.knodaInfo.avatar.small != null)
                avatar.setImageUrl(userContact.knodaInfo.avatar.small, parent.networkingManager.getImageLoader());
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
                    if(!isChecked)
                        adapter.unchecking=true;
                    adapter.notifyDataSetChanged();
                }
            });
        } else {
            //invite
            checkBox.setVisibility(GONE);
            plusBtn.setVisibility(VISIBLE);
            avatar.setVisibility(GONE);
            findViewById(R.id.findfriends_listcell_avatar_container).setVisibility(GONE);
            String d = "";
            if (userContact.phones != null && userContact.phones.size() > 0) {
                for (String s : userContact.phones) {
                    if (s.length() > 6) {
                        String phone = "(" + s.substring(0, 3) + ") " + s.substring(3, 6) + "-" + s.substring(6);
                        d += phone + ", ";
                    }
                }
            }
            if (userContact.emails != null && userContact.emails.size() > 0) {
                for (String s : userContact.emails) {
                    d += s + ", ";
                }
            }
            if (d.length() > 2)
                description.setText(d.substring(0, d.length() - 2));
            if (parent.inviting.containsKey(userContact.contact_id)) {
                plusBtn.setBackgroundResource(R.drawable.ic_invite_active);
                GroupInvitation g = parent.inviting.get(userContact.contact_id);
                if (g.phoneNumber != null)
                    description.setText(g.phoneNumber);
                else if (g.email != null)
                    description.setText(g.email);
            } else
                plusBtn.setBackgroundResource(R.drawable.ic_invite);
            plusBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (parent.inviting.containsKey(userContact.contact_id)) {
                        parent.inviting.remove(userContact.contact_id);
                        plusBtn.setBackgroundResource(R.drawable.ic_invite);
                        parent.setSubmitBtnText();
                        adapter.notifyDataSetChanged();
                    } else {

                        final HashSet<String> options = new HashSet<String>();
                        if (userContact.emails != null) {
                            for (String s : userContact.emails) {
                                options.add(s);
                            }
                        }
                        if (userContact.phones != null) {
                            for (String s : userContact.phones) {
                                options.add(s);
                            }
                        }


                        if (options.size() == 0) {
                            return;
                        } else if (options.size() == 1) {
                            GroupInvitation groupInvitation = new GroupInvitation();
                            String temp = (String) options.toArray()[0];
                            if (temp.indexOf("@") != -1)
                                groupInvitation.email = temp;
                            else
                                groupInvitation.phoneNumber = temp;
                            parent.inviting.put(userContact.contact_id, groupInvitation);
                            plusBtn.setBackgroundResource(R.drawable.ic_invite_active);
                            parent.setSubmitBtnText();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                            final CharSequence[] array = options.toArray(new CharSequence[0]);
                            builder.setTitle("Choose a contact method for " + userContact.contact_id);
                            builder.setItems(array, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GroupInvitation groupInvitation = new GroupInvitation();
                                    String temp = (String) options.toArray()[which];
                                    if (temp.indexOf("@") != -1)
                                        groupInvitation.email = temp;
                                    else
                                        groupInvitation.phoneNumber = temp;
                                    parent.inviting.put(userContact.contact_id, groupInvitation);
                                    plusBtn.setBackgroundResource(R.drawable.ic_invite_active);
                                    parent.setSubmitBtnText();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();

                        }
                    }
                }
            });
        }
    }
}

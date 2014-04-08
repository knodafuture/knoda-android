package views.group;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import adapters.MembershipAdapter;
import butterknife.InjectView;
import butterknife.OnClick;
import factories.GsonF;
import models.Group;
import models.Member;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.ChangeGroupEvent;
import pubsub.GroupChangedEvent;
import views.core.BaseFragment;

public class GroupSettingsFragment extends BaseFragment implements MembershipCell.MembershipCellCallbacks {

    @InjectView(R.id.group_settings_avatar)
    NetworkImageView avatarImageView;

    @InjectView(R.id.group_settings_description_textview)
    TextView descriptionTextView;

    @InjectView(R.id.group_settings_name_textview)
    TextView nameTextView;

    @InjectView(R.id.group_settings_list_view)
    ListView listView;

    @InjectView(R.id.group_settings_edit_group_button)
    RelativeLayout editGroupButton;

    @InjectView(R.id.group_settings_join_group_button)
    RelativeLayout joinGroupButton;

    @InjectView(R.id.group_settings_leave_group_button)
    RelativeLayout leaveGroupButton;

    @InjectView(R.id.group_settings_invite_view)
    LinearLayout inviteView;

    private Group group;
    private String invitationCode;

    @Subscribe
    public void groupChanged(GroupChangedEvent event) {
        group = event.group;
        populate();
    }

    @OnClick(R.id.group_settings_invite_button) void onInvite() {
        InvitationsFragment fragment = InvitationsFragment.newInstance(group);
        pushFragment(fragment);
    }

    @OnClick(R.id.group_settings_share_button) void onShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String text = "Join my group " + group.name + " on Knoda! " + group.shareUrl;
        share.putExtra(Intent.EXTRA_TEXT, text);
        String subject = userManager.getUser().username + " invited you to join a group on Knoda";
        share.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(Intent.createChooser(share, "How would you like to share?"));
    }

    @OnClick(R.id.group_settings_leave_group_button) void onLeave() {
        leaveGroup();
    }

    @OnClick(R.id.group_settings_join_group_button) void onJoin() {
        joinGroup();
    }

    @OnClick(R.id.group_settings_edit_group_button) void onEdit() {
        EditGroupFragment fragment = EditGroupFragment.newInstance(group);
        pushFragment(fragment);
    }

    public static GroupSettingsFragment newInstance(Group group, String invitationCode) {
        GroupSettingsFragment fragment = new GroupSettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP", GsonF.actory().toJson(group));
        if (invitationCode != null) {
            bundle.putString("INVITATION_CODE", invitationCode);
        }
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
        if (getArguments().containsKey("INVITATION_CODE")) {
            invitationCode = getArguments().getString("INVITATION_CODE");
        }
        bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_settings, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("SETTINGS");
        populate();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new ChangeGroupEvent(group));
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.post(new ChangeGroupEvent(null));
    }

    private void populate() {
        if (group.avatar != null && group.avatar.small != null)
            avatarImageView.setImageUrl(group.avatar.small, networkingManager.getImageLoader());

        nameTextView.setText(group.name);
        descriptionTextView.setText(group.description);

        if (group.myMembership == null && invitationCode != null) {
            inviteView.setVisibility(View.INVISIBLE);
            editGroupButton.setVisibility(View.GONE);
            leaveGroupButton.setVisibility(View.INVISIBLE);
            joinGroupButton.setVisibility(View.VISIBLE);
        } else {
            joinGroupButton.setVisibility(View.INVISIBLE);

            if (userManager.getUser().id.equals(group.owner)) {
                inviteView.setVisibility(View.VISIBLE);
                leaveGroupButton.setVisibility(View.INVISIBLE);
            } else {
                inviteView.setVisibility(View.INVISIBLE);
                editGroupButton.setVisibility(View.GONE);
                leaveGroupButton.setVisibility(View.VISIBLE);
            }
        }

        if (group.myMembership != null || invitationCode == null)
            refresh();
    }
    private void refresh() {
        networkingManager.getMembersInGroup(group.id, new NetworkListCallback<Member>() {
            @Override
            public void completionHandler(ArrayList<Member> object, ServerError error) {
                if (error != null)
                    return;
                setMembers(object);
            }
        });
    }

    private void setMembers(ArrayList<Member> members) {
        listView.setAdapter(new MembershipAdapter(getActivity(), group, this, members));
    }

    @Override
    public void memberRemovedAtPosition(int position) {
        Member member = (Member) listView.getAdapter().getItem(position);
        networkingManager.deleteMembership(member.id, new NetworkCallback<Member>() {
            @Override
            public void completionHandler(Member object, ServerError error) {
                refresh();
            }
        });
    }

    private void leaveGroup() {
        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setTitle("Are you sure you wish to leave the group?")
                .create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                spinner.show();
                networkingManager.deleteMembership(group.myMembership.id, new NetworkCallback<Member>() {
                    @Override
                    public void completionHandler(Member object, ServerError error) {
                        userManager.refreshGroups(new NetworkListCallback<Group>() {
                            @Override
                            public void completionHandler(ArrayList<Group> object, ServerError error) {
                                spinner.hide();
                                popToRootFragment();
                            }
                        });
                    }
                });
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });

    }

    public void joinGroup() {
        spinner.show();
        networkingManager.joinGroup(invitationCode, group.id, new NetworkCallback<Member>() {
            @Override
            public void completionHandler(Member object, ServerError error) {
                if (error != null) {
                    spinner.hide();
                    errorReporter.showError("Unable to join the group at this time.");
                } else {
                    userManager.refreshGroups(new NetworkListCallback<Group>() {
                        @Override
                        public void completionHandler(ArrayList<Group> object, ServerError error) {
                            spinner.hide();
                            group = userManager.getGroupById(group.id);
                            invitationCode = null;
                            populate();
                        }
                    });
                }
            }
        });
    }
}

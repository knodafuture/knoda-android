package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import helpers.AdapterHelper;
import models.GroupInvitation;
import models.UserContact;
import views.contacts.FindFriendsActivity;
import views.contacts.FindFriendsListCell;
import views.contacts.FindFriendsListCellHeader;

public class UserContactAdapter extends PagingAdapter<UserContact> {

    public int type;
    FindFriendsActivity parent;

    public UserContactAdapter(int type, Context context, PagingAdapterDatasource<UserContact> datasource, ImageLoader imageLoader, FindFriendsActivity activity) {
        super(context, datasource, imageLoader);
        this.type = type;
        this.parent = activity;
    }

    @Override
    public int getCount() {
        if (objects.size() == 0)
            return super.getCount();
        if (objects.get(0).knodaInfo != null && objects.get(objects.size() - 1).knodaInfo == null)
            return super.getCount() + 2;
        else
            return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (objects.size() == 0)
            return super.getView(position, convertView, parent);

        if (position >= objects.size() + 1)
            return super.getView(position, convertView, parent);

        //intial header
        if (position == 0) {
            FindFriendsListCellHeader listCellHeader = (FindFriendsListCellHeader) AdapterHelper.getConvertViewSafely(convertView, FindFriendsListCellHeader.class);
            if (listCellHeader == null)
                listCellHeader = (FindFriendsListCellHeader) LayoutInflater.from(context).inflate(R.layout.list_cell_findfriends_follow_header, null);
            if (type == FindFriendsListCellHeader.CONTACTS) {
                if (objects.get(0).knodaInfo != null)
                    listCellHeader.setMode(FindFriendsListCellHeader.CONTACTS, this);
                else
                    listCellHeader.setMode(FindFriendsListCellHeader.INVITE, this);
            } else
                listCellHeader.setMode(type, this);
            return listCellHeader;
            //potential second header
        } else if ((objects.get(position - 1).knodaInfo == null && position - 2 >= 0 &&
                objects.get(position - 2).knodaInfo != null)) {
            FindFriendsListCellHeader listCellHeader = (FindFriendsListCellHeader) AdapterHelper.getConvertViewSafely(convertView, FindFriendsListCellHeader.class);
            if (listCellHeader == null)
                listCellHeader = (FindFriendsListCellHeader) LayoutInflater.from(context).inflate(R.layout.list_cell_findfriends_follow_header, null);
            listCellHeader.setMode(FindFriendsListCellHeader.INVITE, this);
            return listCellHeader;
            //list item
        } else {
            FindFriendsListCell listItem = (FindFriendsListCell) AdapterHelper.getConvertViewSafely(convertView, FindFriendsListCell.class);
            if (listItem == null)
                listItem = (FindFriendsListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_findfriends_follow, null);
            final UserContact userContact = objects.get(offset(position));
            if (userContact != null)
                listItem.setUser(userContact, this, this.parent);
            return listItem;
        }
    }

    @Override
    public boolean canLoadNextPage() {
        return false;
    }

    public void followAll(boolean checked) {
        if (type == FindFriendsListCellHeader.CONTACTS)
            parent.following.clear();
        else if (type == FindFriendsListCellHeader.FACEBOOK)
            parent.followingFacebook.clear();
        else if (type == FindFriendsListCellHeader.TWITTER)
            parent.followingTwitter.clear();

        if (!checked) {
            notifyDataSetChanged();
            parent.setSubmitBtnText();
            return;
        }
        for (int i = 0; i != objects.size(); i++) {
            if (objects.get(i).knodaInfo != null) {
                if (type == FindFriendsListCellHeader.CONTACTS)
                    parent.following.put(objects.get(i).contact_id, objects.get(i).knodaInfo);
                else if (type == FindFriendsListCellHeader.FACEBOOK)
                    parent.followingFacebook.put(objects.get(i).contact_id, objects.get(i).knodaInfo);
                else if (type == FindFriendsListCellHeader.TWITTER)
                    parent.followingTwitter.put(objects.get(i).contact_id, objects.get(i).knodaInfo);
            }
        }
        notifyDataSetChanged();
        parent.setSubmitBtnText();
    }

    public void inviteAll(boolean checked) {
        parent.inviting.clear();
        if (!checked) {
            notifyDataSetChanged();
            parent.setSubmitBtnText();
            return;
        }
        for (int i = 0; i != objects.size(); i++) {
            if (objects.get(i).knodaInfo == null) {
                UserContact userContact = objects.get(i);
                GroupInvitation groupInvitation = new GroupInvitation();
                if (userContact.emails != null && userContact.emails.size() > 0)
                    groupInvitation.email = (String) userContact.emails.toArray()[0];
                if (userContact.phones != null && userContact.phones.size() > 0)
                    groupInvitation.phoneNumber = (String) userContact.phones.toArray()[0];
                parent.inviting.put(userContact.contact_id, groupInvitation);
            }
        }
        notifyDataSetChanged();
        parent.setSubmitBtnText();
    }

    public int offset(int position) {
        if (objects.size() == 0)
            return position - 0;
        if (objects.get(0).knodaInfo != null && objects.get(objects.size() - 1).knodaInfo == null) {
            if (objects.get(position - 1).knodaInfo == null)
                return position - 2;
            else
                return position - 1;
        } else {
            return position - 1;
        }
    }

    public boolean followAll(int type) {
        if (type == FindFriendsListCellHeader.CONTACTS && objects.size() == parent.following.size())
            return true;
        if (type == FindFriendsListCellHeader.FACEBOOK && objects.size() == parent.followingFacebook.size())
            return true;
        if (type == FindFriendsListCellHeader.TWITTER && objects.size() == parent.followingTwitter.size())
            return true;
        return false;
    }

    @Override
    protected View getNoContentView() {
        View view;
        if (type == FindFriendsListCellHeader.FACEBOOK) {
            view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content_invite_facebook, null);
            view.findViewById(R.id.no_content_facebook_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.addFBAccount();
                }
            });
        } else if (type == FindFriendsListCellHeader.TWITTER) {
            view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content_invite_twitter, null);
            view.findViewById(R.id.no_content_twitter_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.addTwitterAccount();
                }
            });
        } else
            view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content, null);
        return view;
    }

}


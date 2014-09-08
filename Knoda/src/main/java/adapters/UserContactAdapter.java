package adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import java.util.ArrayList;

import helpers.AdapterHelper;
import models.BaseModel;
import models.GroupInvitation;
import models.ServerError;
import models.UserContact;
import networking.NetworkCallback;
import views.contacts.FindFriendsActivity;
import views.contacts.FindFriendsListCell;
import views.contacts.FindFriendsListCellHeader;

public class UserContactAdapter extends PagingAdapter<UserContact> {

    public int type;
    FindFriendsActivity findFriendsActivity;
    ArrayList<UserContact> searchedContacts = new ArrayList<UserContact>();
    ArrayList<UserContact> allContacts;
    public int followSize=-1;

    public UserContactAdapter(int type, Context context, PagingAdapterDatasource<UserContact> datasource, ImageLoader imageLoader, FindFriendsActivity activity) {
        super(context, datasource, imageLoader);
        this.type = type;
        this.findFriendsActivity = activity;
    }

    @Override
    public int getCount() {
        int add = 0;
        if (type == FindFriendsListCellHeader.FACEBOOK || type == FindFriendsListCellHeader.TWITTER)
            add = 1;
        if (objects.size() == 0)
            return super.getCount();
        if (objects.get(0).knodaInfo != null && objects.get(objects.size() - 1).knodaInfo == null)
            return super.getCount() + 2 + add;
        else
            return super.getCount() + 1 + add;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (objects.size() == 0)
            return super.getView(position, convertView, parent);

        if (position == objects.size() + 1) {
            if (type == FindFriendsListCellHeader.FACEBOOK) {
                View v = LayoutInflater.from(context).inflate(R.layout.list_cell_share_facebook, null);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareFacebook();
                    }
                });
                v.findViewById(R.id.no_content_facebook_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareFacebook();
                    }
                });
                return v;
            } else if (type == FindFriendsListCellHeader.TWITTER) {
                View v = LayoutInflater.from(context).inflate(R.layout.list_cell_share_twitter, null);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareTwitter();
                    }
                });
                v.findViewById(R.id.no_content_facebook_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareTwitter();
                    }
                });
                return v;
            }
        }


        if (position > getCount() - 1)
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
        } else if (position < objects.size() && (objects.get(position - 1).knodaInfo == null && position - 2 >= 0 &&
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
                listItem.setUser(userContact, this, this.findFriendsActivity);
            return listItem;
        }
    }

    @Override
    public boolean canLoadNextPage() {
        return false;
    }

    public void followAll(boolean checked) {
        if (type == FindFriendsListCellHeader.CONTACTS)
            findFriendsActivity.following.clear();
        else if (type == FindFriendsListCellHeader.FACEBOOK)
            findFriendsActivity.followingFacebook.clear();
        else if (type == FindFriendsListCellHeader.TWITTER)
            findFriendsActivity.followingTwitter.clear();

        if (!checked) {
            notifyDataSetChanged();
            findFriendsActivity.setSubmitBtnText();
            return;
        }
        for (int i = 0; i != objects.size(); i++) {
            if (objects.get(i).knodaInfo != null) {
                if (type == FindFriendsListCellHeader.CONTACTS)
                    findFriendsActivity.following.put(objects.get(i).contact_id, objects.get(i).knodaInfo);
                else if (type == FindFriendsListCellHeader.FACEBOOK)
                    findFriendsActivity.followingFacebook.put(objects.get(i).contact_id, objects.get(i).knodaInfo);
                else if (type == FindFriendsListCellHeader.TWITTER)
                    findFriendsActivity.followingTwitter.put(objects.get(i).contact_id, objects.get(i).knodaInfo);
            }
        }
        notifyDataSetChanged();
        findFriendsActivity.setSubmitBtnText();
    }

    public void inviteAll(boolean checked) {
        findFriendsActivity.inviting.clear();
        if (!checked) {
            notifyDataSetChanged();
            findFriendsActivity.setSubmitBtnText();
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
                findFriendsActivity.inviting.put(userContact.contact_id, groupInvitation);
            }
        }
        notifyDataSetChanged();
        findFriendsActivity.setSubmitBtnText();
    }

    public int offset(int position) {
        if (objects.size() == 0)
            return position - 0;
        if (objects.get(0).knodaInfo != null && objects.get(objects.size() - 1).knodaInfo == null) {
            if (position-1>=objects.size() || objects.get(position - 1).knodaInfo == null)
                return position - 2;
            else
                return position - 1;
        } else {
            return position - 1;
        }
    }

    public boolean followAll(int type) {
        if (type == FindFriendsListCellHeader.CONTACTS && followSize == findFriendsActivity.following.size())
            return true;
        if (type == FindFriendsListCellHeader.FACEBOOK && objects.size() == findFriendsActivity.followingFacebook.size())
            return true;
        if (type == FindFriendsListCellHeader.TWITTER && objects.size() == findFriendsActivity.followingTwitter.size())
            return true;
        return false;
    }

    @Override
    protected View getNoContentView() {
        View view;
        if (type == FindFriendsListCellHeader.FACEBOOK) {
            view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content_invite_facebook, null);

            if (findFriendsActivity.userManager.getUser().getFacebookAccount() != null) {
                ((TextView) view.findViewById(R.id.no_content_textview2)).setText(datasource.noContentString());
                ((TextView) view.findViewById(R.id.no_content_facebook_btn_text)).setText("Share on Facebook");
                view.findViewById(R.id.no_content_facebook_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareFacebook();
                    }
                });
            } else {
                view.findViewById(R.id.no_content_facebook_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findFriendsActivity.addFBAccount();
                    }
                });
            }
        } else if (type == FindFriendsListCellHeader.TWITTER) {
            view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content_invite_twitter, null);

            if (findFriendsActivity.userManager.getUser().getTwitterAccount() != null) {
                ((TextView) view.findViewById(R.id.no_content_textview2)).setText(datasource.noContentString());
                ((TextView) view.findViewById(R.id.no_content_twitter_btn_text)).setText("Share on Twitter");
                view.findViewById(R.id.no_content_twitter_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareTwitter();
                    }
                });
            } else {
                view.findViewById(R.id.no_content_twitter_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findFriendsActivity.addTwitterAccount();
                    }
                });
            }
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content, null);
            ((TextView) view.findViewById(R.id.no_content_textview)).setText(datasource.noContentString());
        }
        return view;
    }


    private void shareFacebook() {
        LayoutInflater li = findFriendsActivity.getLayoutInflater();
        final View postView = li.inflate(R.layout.dialog_post, null);
        final EditText msg = (EditText) postView.findViewById(R.id.message);
        msg.setHint("Facebook post");
        final AlertDialog alert = new AlertDialog.Builder(findFriendsActivity)
                .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        findFriendsActivity.networkingManager.postFacebook(msg.getText().toString(), new NetworkCallback<BaseModel>() {
                            @Override
                            public void completionHandler(BaseModel object, ServerError error) {
                                if (error == null)
                                    Toast.makeText(findFriendsActivity, "Facebook post successful!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .setView(postView)
                .setTitle("Share on Facebook")
                .create();
        alert.show();
    }

    private void shareTwitter() {
        LayoutInflater li = findFriendsActivity.getLayoutInflater();
        final View postView = li.inflate(R.layout.dialog_post, null);
        final EditText msg = (EditText) postView.findViewById(R.id.message);
        msg.setHint("Tweet");
        msg.setFilters(new InputFilter[]{new InputFilter.LengthFilter(140)});
        final AlertDialog alert = new AlertDialog.Builder(findFriendsActivity)
                .setPositiveButton("Tweet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        findFriendsActivity.networkingManager.postTwitter(msg.getText().toString(), new NetworkCallback<BaseModel>() {
                            @Override
                            public void completionHandler(BaseModel object, ServerError error) {
                                if (error == null)
                                    Toast.makeText(findFriendsActivity, "Tweet successful!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .setView(postView)
                .setTitle("Share on Twitter")
                .create();
        alert.show();
    }

    public void searchFor(String searchterm) {
        if (allContacts == null) {
            allContacts = objects;
        } else
            objects = allContacts;
        if (searchterm.length() == 0) {
            resetSearch();
            return;
        }

        searchedContacts.clear();
        for (UserContact u : objects) {
            if (u.contact_id.toLowerCase().indexOf(searchterm.toLowerCase()) != -1) {
                searchedContacts.add(u);
            }
        }
        objects = searchedContacts;
        notifyDataSetChanged();
    }

    public void resetSearch() {
        if (searchedContacts != null)
            searchedContacts.clear();
        if (allContacts != null)
            objects = allContacts;
        notifyDataSetChanged();
    }


}
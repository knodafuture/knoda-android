package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.Collections;

import helpers.AdapterHelper;
import models.Contact;
import models.InvitationHolder;
import models.User;
import views.group.InvitationsListCell;

/**
 * Created by nick on 4/7/14.
 */
public class InvitationsSearchAdapter extends BaseAdapter {

    private ArrayList<InvitationHolder> objects = new ArrayList<InvitationHolder>();
    private ArrayList<Contact> contactResults = new ArrayList<Contact>();
    private ArrayList<User> knodaResults = new ArrayList<User>();
    private Context context;

    public InvitationsSearchAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {

        if (objects.size() == 0)
            return 1;

        return objects.size();
    }

    @Override
    public InvitationHolder getItem(int position) {
        if (position >= objects.size())
            return null;
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (objects.size() == 0)
            return LayoutInflater.from(context).inflate(R.layout.list_cell_loading, null);

        InvitationHolder holder = getItem(position);

        InvitationsListCell cell = (InvitationsListCell) AdapterHelper.getConvertViewSafely(convertView, InvitationsListCell.class);

        if (cell == null)
            cell = new InvitationsListCell(context);


        cell.setInvitationHolder(holder, position);
        return cell;
    }

    public void addKnodaUsers(ArrayList<User> users) {
        this.knodaResults = users;
        sortAndUpdate();
    }

    public void addContacts(ArrayList<Contact> contacts) {
        this.contactResults = contacts;
        sortAndUpdate();
    }

    private void sortAndUpdate() {

        ArrayList<InvitationHolder> tmp = new ArrayList<InvitationHolder>();

        for (User u : knodaResults) {
            tmp.add(InvitationHolder.withUser(u));
        }

        for (Contact c : contactResults) {
            tmp.add(InvitationHolder.withContact(c));
        }

        Collections.sort(tmp, InvitationHolder.comparator());

        objects = tmp;
        notifyDataSetChanged();
    }


}

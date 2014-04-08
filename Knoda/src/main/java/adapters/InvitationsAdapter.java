package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.Collections;

import helpers.AdapterHelper;
import models.InvitationHolder;
import views.group.InvitationsListCell;

/**
 * Created by nick on 4/7/14.
 */
public class InvitationsAdapter extends BaseAdapter {

    public ArrayList<InvitationHolder> objects = new ArrayList<InvitationHolder>();
    private Context context;
    private InvitationsListCell.InvitationsCellCallbacks callbacks;

    public InvitationsAdapter(Context context, InvitationsListCell.InvitationsCellCallbacks callbacks) {
        this.context = context;
        this.callbacks = callbacks;
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


    public void addInvitation(InvitationHolder holder) {
        if (objects.contains(holder))
            return;
        objects.add(holder);
        Collections.sort(objects, InvitationHolder.comparator());
        notifyDataSetChanged();
    }

    public void removeAtPosition(int position) {
        objects.remove(position);
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (objects.size() == 0)
            return getNoContentView();

        InvitationHolder holder = getItem(position);

        InvitationsListCell cell = (InvitationsListCell) AdapterHelper.getConvertViewSafely(convertView, InvitationsListCell.class);

        if (cell == null)
            cell = new InvitationsListCell(context, callbacks);


        cell.setInvitationHolder(holder, position);
        return cell;
    }

    private View getNoContentView() {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content, null);
        ((TextView)view.findViewById(R.id.no_content_textview)).setText("Find your friends on Knoda using the search bar above. You can also type a name, phone number, or email address to invite them from your contact list.");
        return view;
    }
}

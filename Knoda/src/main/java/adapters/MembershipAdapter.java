package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.knoda.knoda.R;

import java.util.ArrayList;

import helpers.AdapterHelper;
import models.Group;
import models.Member;
import views.group.MembershipCell;

/**
 * Created by nick on 4/6/14.
 */
public class MembershipAdapter extends BaseAdapter {

    private ArrayList<Member> objects = new ArrayList<Member>();
    private Context context;
    private MembershipCell.MembershipCellCallbacks callbacks;
    private Group group;

    public MembershipAdapter(Context context, Group group, MembershipCell.MembershipCellCallbacks callbacks, ArrayList<Member> objects) {
        this.context = context;
        this.callbacks = callbacks;
        this.group = group;
        this.objects = objects;
    }

    @Override
    public int getCount() {

        if (objects.size() == 0)
            return 1;

        return objects.size();
    }

    @Override
    public Member getItem(int position) {
        if (position >= objects.size())
            return null;
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void insertAt(Member object, int index) {
        objects.add(index, object);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (objects.size() == 0)
            return LayoutInflater.from(context).inflate(R.layout.list_cell_loading, null);

        Member member = getItem(position);

        MembershipCell cell = (MembershipCell) AdapterHelper.getConvertViewSafely(convertView, MembershipCell.class);

        if (cell == null)
            cell = new MembershipCell(context, callbacks);

        cell.setMember(member, position, group);

        return cell;
    }
}

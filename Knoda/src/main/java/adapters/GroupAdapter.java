package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import helpers.AdapterHelper;
import managers.UserManager;
import models.Group;
import views.group.CreateGroupHeaderView;
import views.group.GroupListCell;

public class GroupAdapter extends PagingAdapter<Group> {

    @Inject
    public UserManager userManager;
    public CreateGroupHeaderView header;
    public Bus bus;

    public GroupAdapter(Context context, PagingAdapterDatasource<Group> datasource, ImageLoader imageLoader) {
        super(context, datasource, imageLoader);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);

        GroupListCell listItem = (GroupListCell) AdapterHelper.getConvertViewSafely(convertView, GroupListCell.class);
        if (listItem == null)
            listItem = (GroupListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_group, null);

        Group group = getItem(position);

        if (group != null) {
            listItem.setGroup(group);
            if (group.avatar != null && group.avatar.small != null)
                listItem.avatarImageView.setImageUrl(group.avatar.small, imageLoader);
        }
        return listItem;
    }

    public boolean canLoadNextPage() {
        return false;
    }
}


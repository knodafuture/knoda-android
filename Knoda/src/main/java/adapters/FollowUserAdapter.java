package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import helpers.AdapterHelper;
import models.User;
import views.profile.FollowFeedFragment;
import views.profile.FollowUserListCell;

public class FollowUserAdapter extends PagingAdapter<User> {

    ImageLoader imageLoader;
    FollowFeedFragment followFeedFragment;

    public FollowUserAdapter(Context context, PagingAdapterDatasource<User> datasource, ImageLoader imageLoader, FollowFeedFragment followFeedFragment) {
        super(context, datasource, imageLoader);
        this.imageLoader = imageLoader;
        this.followFeedFragment=followFeedFragment;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (position >= objects.size() + 1 || objects.size()==0)
            return super.getView(position, convertView, parent);

        FollowUserListCell listCell = (FollowUserListCell) AdapterHelper.getConvertViewSafely(convertView, FollowUserListCell.class);
        if (listCell == null) {
            listCell = (FollowUserListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_follow_user, null);
        }
        listCell.setUser(objects.get(position),imageLoader,followFeedFragment);

        return listCell;
    }

    @Override
    public boolean canLoadNextPage() {
        return false;
    }

}


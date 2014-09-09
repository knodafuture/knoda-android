package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        this.followFeedFragment = followFeedFragment;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (position >= objects.size() + 1 || objects.size() == 0)
            return super.getView(position, convertView, parent);

        FollowUserListCell listCell = (FollowUserListCell) AdapterHelper.getConvertViewSafely(convertView, FollowUserListCell.class);
        if (listCell == null) {
            listCell = (FollowUserListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_follow_user, null);
        }
        listCell.setUser(objects.get(position), imageLoader, followFeedFragment);

        return listCell;
    }

    @Override
    public boolean canLoadNextPage() {
        return false;
    }

    @Override
    protected View getNoContentView() {
        View view;

        if (followFeedFragment.screenNumber == 1 && followFeedFragment.user.id == followFeedFragment.userManager.getUser().id) {
            view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content_follow_feed, null);
            ((TextView) view.findViewById(R.id.no_content_textview2)).setText(datasource.noContentString());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followFeedFragment.onAddFriendsClick();
                }
            });
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content, null);
            ((TextView) view.findViewById(R.id.no_content_textview)).setText(datasource.noContentString());
        }

        return view;
    }

}


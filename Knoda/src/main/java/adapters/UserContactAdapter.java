package adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import helpers.AdapterHelper;
import models.UserContact;
import views.contacts.FindFriendsListCell;

public class UserContactAdapter extends PagingAdapter<UserContact> {


    public UserContactAdapter(Context context, PagingAdapterDatasource<UserContact> datasource, ImageLoader imageLoader) {
        super(context, datasource, imageLoader);
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);


        FindFriendsListCell listItem = (FindFriendsListCell) AdapterHelper.getConvertViewSafely(convertView, FindFriendsListCell.class);
        if (listItem == null)
            listItem = (FindFriendsListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_findfriends_follow, null);
        final UserContact userContact = objects.get(position);
        if (userContact != null) {
            listItem.setUser(userContact);
//            if (contest.avatar != null) {
//                listItem.avatarImageView.setImageUrl(contest.avatar.big, imageLoader);
//                listItem.titleTV.setLayoutParams(title_normal);
//            } else {
//                listItem.findViewById(R.id.contest_avatar_container).setVisibility(View.GONE);
//                listItem.titleTV.setLayoutParams(title_no_image);
//            }
        }


        return listItem;
    }

    @Override
    public boolean canLoadNextPage() {
        return false;
    }
}


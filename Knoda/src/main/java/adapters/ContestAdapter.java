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
import models.Contest;
import views.contests.ContestListCell;

public class ContestAdapter extends PagingAdapter<Contest> {

    @Inject
    public UserManager userManager;
    public Bus bus;

    public ContestAdapter(Context context, PagingAdapterDatasource<Contest> datasource, ImageLoader imageLoader) {
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


        ContestListCell listItem = (ContestListCell) AdapterHelper.getConvertViewSafely(convertView, ContestListCell.class);
        if (listItem == null)
            listItem = (ContestListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_contest, null);
        Contest contest = objects.get(position);
        if (contest != null) {
            listItem.setContest(contest);
            if (contest.avatar != null)
                listItem.avatarImageView.setImageUrl(contest.avatar, imageLoader);
        }


        return listItem;
    }

    public boolean canLoadNextPage() {
        return false;
    }
}


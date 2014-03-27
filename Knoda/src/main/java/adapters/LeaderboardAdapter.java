package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import helpers.AdapterHelper;
import models.Leader;
import views.group.LeaderListCell;

public class LeaderboardAdapter extends PagingAdapter<Leader> {

    public LeaderboardAdapter(Context context, PagingAdapterDatasource<Leader> datasource, ImageLoader imageLoader) {
        super(context, datasource, imageLoader);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);

        LeaderListCell listItem = (LeaderListCell) AdapterHelper.getConvertViewSafely(convertView, LeaderListCell.class);
        if (listItem == null)
            listItem = (LeaderListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_leader, null);

        Leader item = getItem(position);

        listItem.setLeader(item);
        return listItem;
    }
}

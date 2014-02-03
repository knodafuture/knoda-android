package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import helpers.AdapterHelper;
import models.ActivityItem;
import views.activity.ActivityListCell;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityAdapter extends PagingAdapter<ActivityItem> {

    public ActivityAdapter(LayoutInflater inflater, PagingAdapterDatasource<ActivityItem> datasource, ImageLoader imageLoader) {
        super(inflater, datasource, imageLoader);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);

        ActivityListCell listItem = (ActivityListCell) AdapterHelper.getConvertViewSafely(convertView, ActivityListCell.class);
        if (listItem == null)
            listItem = (ActivityListCell) inflater.inflate(R.layout.list_cell_activity, null);

        ActivityItem item = getItem(position);

        listItem.setActivityItem(item);
        return listItem;
    }


}
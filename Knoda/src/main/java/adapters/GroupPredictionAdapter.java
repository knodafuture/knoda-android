package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.otto.Bus;

import helpers.AdapterHelper;
import models.Group;
import models.Prediction;
import views.group.GroupPredictionListHeader;

public class GroupPredictionAdapter extends PredictionAdapter {

    public Group group;

    public GroupPredictionAdapter(Context context, PagingAdapter.PagingAdapterDatasource<Prediction> datasource, ImageLoader imageLoader) {
        super(context, datasource, imageLoader, new Bus());
    }

    @Override
    public int getCount() {
        if (group == null)
            return super.getCount();

        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (group == null)
            return super.getView(position, convertView, parent);

        if (position == 0)
            return getHeaderView(convertView);

        return super.getView(position - 1, convertView, parent);

    }

    public void setGroup(Group group) {
        this.group = group;
        notifyDataSetChanged();
    }

    View getHeaderView(View convertView) {

        GroupPredictionListHeader header = (GroupPredictionListHeader) AdapterHelper.getConvertViewSafely(convertView, GroupPredictionListHeader.class);

        if (header == null)
            header = new GroupPredictionListHeader(context);

        return header;
    }
}

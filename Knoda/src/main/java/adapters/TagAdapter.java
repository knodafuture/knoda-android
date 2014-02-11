package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import models.Tag;

/**
 * Created by nick on 2/11/14.
 */
public class TagAdapter extends PagingAdapter<Tag> {

    public TagAdapter(LayoutInflater inflater, PagingAdapterDatasource<Tag> datasource, ImageLoader imageLoader) {
        super(inflater, datasource, imageLoader);
        this.inflater = inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);

        View view = inflater.inflate(R.layout.list_cell_tags, null);

        ((TextView)view.findViewById(R.id.tag_list_cell_textview)).setText(getItem(position).name);

        return view;
    }

    @Override
    public boolean canLoadNextPage() {
        return false;
    }
}

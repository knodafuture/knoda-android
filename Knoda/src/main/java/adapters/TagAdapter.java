package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import org.apache.commons.lang3.text.WordUtils;

import models.Tag;

/**
 * Created by nick on 2/11/14.
 */
public class TagAdapter extends PagingAdapter<Tag> {

    public TagAdapter(Context context, PagingAdapterDatasource<Tag> datasource, ImageLoader imageLoader) {
        super(context, datasource, imageLoader);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);

        View view = LayoutInflater.from(context).inflate(R.layout.list_cell_tags, null);

        ((TextView) view.findViewById(R.id.tag_list_cell_textview)).setText(WordUtils.capitalizeFully(getItem(position).name));

        return view;
    }

    @Override
    public boolean canLoadNextPage() {
        return false;
    }
}

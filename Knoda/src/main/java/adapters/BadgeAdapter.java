package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.Badge;

public class BadgeAdapter extends ArrayAdapter<Badge> {
    private ImageLoader imageLoader;

    public BadgeAdapter(Context context, ImageLoader imageLoader) {
        super(context, 0);
        this.imageLoader = imageLoader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View gridView = convertView;

        if (convertView == null)
            gridView = inflater.inflate(R.layout.grid_cell_badges, null);

            NetworkImageView imageView = (NetworkImageView) gridView.findViewById(R.id.image);
            imageView.setImageUrl(getItem(position).url, imageLoader);

        return gridView;
    }
}

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

/**
 * Created by adamengland on 2/14/14.
 */
public class BadgeAdapter extends ArrayAdapter<Badge> {
    public ImageLoader imageLoader;

    public BadgeAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        if (convertView == null) {
            gridView = new View(getContext());
            gridView = inflater.inflate(R.layout.grid_cell_badges, null);
            NetworkImageView imageView = (NetworkImageView) gridView
                    .findViewById(R.id.image);
            imageView.setImageUrl("http://www.knoda.com/assets/badges/1_prediction-5c58ff866138f6d8831b3c979395c03e.png", imageLoader);
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }
}

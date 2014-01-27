package views.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.knoda.knoda.R;

import java.util.ArrayList;

import core.KnodaScreen;

/**
 * Created by nick on 1/27/14.
 */
public class NavigationAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private ArrayList<KnodaScreen> screens;

    private final class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

    public NavigationAdapter(LayoutInflater inflater, ArrayList<KnodaScreen> screens) {
        this.inflater = inflater;
        this.screens = screens;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NavigationListCell listItem = (NavigationListCell) convertView;
        if (listItem == null)
            listItem = (NavigationListCell) inflater.inflate(R.layout.list_navigation, null);

        KnodaScreen screen = getItem(position);

        listItem.labelTextView.setText(screen.displayName);
        listItem.iconImageView.setImageDrawable(screen.drawable);

        return listItem;
    }

    @Override
    public int getCount() {
        return screens.size();
    }

    @Override
    public KnodaScreen getItem(int position) {
        return screens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private View addViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.imageView = (ImageView) view.findViewById(R.id.navigation_list_icon);
        holder.textView = (TextView) view.findViewById(R.id.navigation_list_text);

        view.setTag(holder);
        return view;
    }
}

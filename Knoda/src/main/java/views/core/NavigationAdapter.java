package views.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.knoda.knoda.R;

import java.util.ArrayList;

import core.KnodaScreen;

/**
 * Created by nick on 1/27/14.
 */
public class NavigationAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private ArrayList<KnodaScreen> screens;

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
}

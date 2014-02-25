package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.knoda.knoda.R;

import java.util.ArrayList;

import models.KnodaScreen;
import models.User;
import views.core.NavigationListCell;

/**
 * Created by nick on 1/27/14.
 */
public class NavigationAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<KnodaScreen> screens;

    private Integer alertsCount = 0;
    private User user;

    public NavigationAdapter(Context context, ArrayList<KnodaScreen> screens) {
        this.context = context;
        this.screens = screens;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NavigationListCell listItem = (NavigationListCell) convertView;
        if (listItem == null)
            listItem = (NavigationListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_navigation, null);

        KnodaScreen screen = getItem(position);

        if (position == KnodaScreen.KnodaScreenOrder.PROFILE.ordinal() && user != null)
            listItem.labelTextView.setText(user.username);
        else
            listItem.labelTextView.setText(screen.displayName);
        listItem.iconImageView.setImageDrawable(screen.drawable);

        if (position == KnodaScreen.KnodaScreenOrder.ACTIVITY.ordinal() && alertsCount > 0) {
            listItem.rightTextView.setVisibility(View.VISIBLE);
            listItem.rightTextView.setText(alertsCount.toString());
        } else
            listItem.rightTextView.setVisibility(View.INVISIBLE);


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


    public void setAlertsCount(Integer alertCount) {
        this.alertsCount = alertCount;
        notifyDataSetChanged();
    }

    public void setUser(User user) {
        this.user = user;
        notifyDataSetChanged();
    }
}

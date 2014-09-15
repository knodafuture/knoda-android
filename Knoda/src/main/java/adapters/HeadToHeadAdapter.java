package adapters;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import helpers.AdapterHelper;
import models.User;
import views.core.MainActivity;
import views.profile.HeadToHeadListCell;

public class HeadToHeadAdapter extends PagingAdapter<User> {

    MainActivity mainActivity;
    LinearLayout.LayoutParams title_normal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams title_no_image = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    int maxBarPixels = 0;
    int barHeight = 0;


    public HeadToHeadAdapter(Context context, PagingAdapterDatasource<User> datasource, ImageLoader imageLoader, MainActivity mainActivity) {
        super(context, datasource, imageLoader);
        this.mainActivity = mainActivity;
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        title_no_image.setMargins(onedp * 15, onedp * 15, onedp * 15, 0);
        title_normal.setMargins(onedp * 15, onedp * 5, onedp * 15, 0);

        Display display = mainActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Point size = new Point();
        display.getSize(size);

        maxBarPixels = (size.x / 2) - (70 * onedp);
        barHeight = 35 * onedp;

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);


        HeadToHeadListCell listItem = (HeadToHeadListCell) AdapterHelper.getConvertViewSafely(convertView, HeadToHeadListCell.class);
        if (listItem == null)
            listItem = (HeadToHeadListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_head_to_head, null);
        final User user = objects.get(position);
        if (user != null) {
            listItem.setUsers(mainActivity.userManager.getUser(), user, mainActivity, maxBarPixels, barHeight);
        }

        return listItem;
    }

    public boolean canLoadNextPage() {
        return false;
    }
}


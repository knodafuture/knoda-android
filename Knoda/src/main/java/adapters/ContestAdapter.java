package adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import helpers.AdapterHelper;
import managers.UserManager;
import models.Contest;
import views.contests.ContestListCell;
import views.core.MainActivity;

public class ContestAdapter extends PagingAdapter<Contest> {

    @Inject
    public UserManager userManager;
    public Bus bus;
    public MainActivity mainActivity;
    boolean explore = false;

    LinearLayout.LayoutParams title_normal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams title_no_image = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    public ContestAdapter(Context context, PagingAdapterDatasource<Contest> datasource, ImageLoader imageLoader, boolean explore) {
        super(context, datasource, imageLoader);
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        title_no_image.setMargins(onedp * 15, onedp * 15, onedp * 15, 0);
        title_normal.setMargins(onedp * 15, onedp * 5, onedp * 15, 0);
        this.explore = explore;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);


        ContestListCell listItem = (ContestListCell) AdapterHelper.getConvertViewSafely(convertView, ContestListCell.class);
        if (listItem == null)
            listItem = (ContestListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_contest, null);
        final Contest contest = objects.get(position);
        if (contest != null) {
            listItem.setContest(contest, mainActivity, explore);
            if (contest.avatar != null) {
                listItem.avatarImageView.setImageUrl(contest.avatar.big, imageLoader);
                listItem.titleTV.setLayoutParams(title_normal);
            } else {
                listItem.findViewById(R.id.contest_avatar_container).setVisibility(View.GONE);
                listItem.titleTV.setLayoutParams(title_no_image);
            }
        }


        return listItem;
    }

    @Override
    protected View getNoContentView() {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content_contest, null);
        ((TextView) view.findViewById(R.id.no_content_textview)).setText(datasource.noContentString());
        return view;
    }

    public boolean canLoadNextPage() {
        return false;
    }
}


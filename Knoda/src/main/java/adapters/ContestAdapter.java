package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public ContestAdapter(Context context, PagingAdapterDatasource<Contest> datasource, ImageLoader imageLoader) {
        super(context, datasource, imageLoader);
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
            listItem.setContest(contest, mainActivity);
            if (contest.avatar != null)
                listItem.avatarImageView.setImageUrl(contest.avatar.big, imageLoader);

            if (contest.contestStages != null && contest.contestStages.size() > 0) {
                //listItem.arrow.setVisibility(View.VISIBLE);
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


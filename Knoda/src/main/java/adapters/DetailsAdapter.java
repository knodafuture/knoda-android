package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;

import models.BaseModel;
import views.details.DetailsActionbar;
import views.details.DetailsHeaderView;

/**
 * Created by nick on 2/13/14.
 */
public class DetailsAdapter<T extends BaseModel> extends PagingAdapter<T> {

    private DetailsHeaderView headerView;
    private DetailsActionbar actionBar;

    public DetailsAdapter(LayoutInflater inflater, PagingAdapterDatasource datasource, ImageLoader imageLoader) {
        super(inflater, datasource, imageLoader);
    }

    public void setHeader(DetailsHeaderView headerView) {
        this.headerView = headerView;
    }

    public void setActionBar(DetailsActionbar actionBar) {
        this.actionBar = actionBar;
    }


    @Override
    public int getCount() {
        return super.getCount() + 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        position = untransformPosition(position);

        if (position == 0)
            return headerView;

        if (position == 1)
            return actionBar;

        return super.getView(position, convertView, parent);

    }

    protected int transformPosition(int position) {
        return position - 2;
    }

    protected int untransformPosition(int position) {
        return position + 2;
    }


}

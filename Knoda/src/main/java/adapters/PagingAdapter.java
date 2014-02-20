package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import java.util.ArrayList;

import managers.NetworkingManager;
import models.BaseModel;
import models.ServerError;
import networking.NetworkListCallback;

/**
 * Created by nick on 2/1/14.
 */
public class PagingAdapter<T extends BaseModel> extends BaseAdapter {

    protected ArrayList<T> objects = new ArrayList<T>();
    protected PagingAdapterDatasource datasource;
    protected PagingAdapaterPageLoadFinishListener onLoadFinished;
    protected Context context;
    public ImageLoader imageLoader;

    public Integer currentPage;
    public boolean loading;

    public interface PagingAdapterDatasource <T extends BaseModel> {
        void getObjectsAfterObject(T object, NetworkListCallback<T> callback);
    }

    public interface  PagingAdapaterPageLoadFinishListener <T extends BaseModel> {
        void adapterFinishedLoadingPage(int page);
    }

    public PagingAdapter(Context context, PagingAdapterDatasource datasource, ImageLoader imageLoader) {
        this.datasource = datasource;
        this.imageLoader = imageLoader;
        this.currentPage = 0;
        this.context = context;
    }

    public void setLoadFinishedListener(PagingAdapaterPageLoadFinishListener<T> onLoadFinished) {
        this.onLoadFinished = onLoadFinished;
    }

    @Override
    public int getCount() {

        if (objects.size() == 0)
            return 1;

        if (canLoadNextPage())
            return objects.size() + 1;

        return objects.size();
    }

    @Override
    public T getItem(int position) {
        if (position >= objects.size())
            return null;
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_cell_loading, null);
        return view;
    }

    public void loadPage(final int page) {

        if (loading)
            return;

        T object = null;
        if (page != 0)
            object = objects.size() == 0 ? null : objects.get(objects.size() - 1);
        loading = true;

        datasource.getObjectsAfterObject(object, new NetworkListCallback<T>() {
            @Override
            public void completionHandler(ArrayList<T> objectsToAdd, ServerError error) {
                loading = false;

                if (error != null || objectsToAdd == null || objectsToAdd.size() == 0)
                    return;

                if (page == 0)
                    objects = objectsToAdd;
                else
                    objects.addAll(objectsToAdd);

                currentPage = page;
                notifyDataSetChanged();

                if (onLoadFinished != null)
                    onLoadFinished.adapterFinishedLoadingPage(page);
            }
        });
    }

    public boolean canLoadNextPage() {
        double div = (double)objects.size() / (double) NetworkingManager.PAGE_LIMIT;

        if (div >= Math.ceil(div))
            return true;

        return false;
    }

    public AbsListView.OnScrollListener makeScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisible, int visibleCount, int totalCount) {
                boolean shouldLoadMore = firstVisible + visibleCount >= totalCount;

                if (shouldLoadMore)
                    loadPage(currentPage + 1);
            }
        };
    }

}

package adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.TimeoutError;
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

    public ImageLoader imageLoader;
    public Integer currentPage;
    public boolean loading;
    public boolean noObjectsRetrieved;
    protected ArrayList<T> objects = new ArrayList<T>();
    protected PagingAdapterDatasource datasource;
    protected PagingAdapaterPageLoadFinishListener onLoadFinished;
    protected Context context;

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

    public void insertAt(T object, int index) {
        objects.add(index, object);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        parent.setBackgroundColor(Color.TRANSPARENT);
        if (objects.size() == 0 && noObjectsRetrieved)
            return getNoContentView();

        //View view = LayoutInflater.from(context).inflate(R.layout.list_cell_loading, null);
        //return view;
        return new View(parent.getContext());
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

                if (error != null || objectsToAdd == null) {
                    if (onLoadFinished != null)
                        onLoadFinished.adapterFinishedLoadingPage(page);
                    if (error.underlyingError instanceof TimeoutError) {
                        try {
                            Log.e("Timeout", error.getDescription());
                        } catch (Exception e) {
                        }
                    }
                    return;
                }

                if (page == 0) {
                    noObjectsRetrieved = objectsToAdd.size() == 0;
                    objects = objectsToAdd;
                } else
                    objects.addAll(objectsToAdd);

                currentPage = page;
                notifyDataSetChanged();

                if (onLoadFinished != null)
                    onLoadFinished.adapterFinishedLoadingPage(page);
            }
        });
    }

    public void setCachedObjects(ArrayList<T> cachedObjects) {
        if (objects.size() == 0) {
            objects = cachedObjects;
            currentPage = 0;
            notifyDataSetChanged();
        }
    }

    public boolean canLoadNextPage() {
        double div = (double) objects.size() / (double) NetworkingManager.PAGE_LIMIT;

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

                if (canLoadNextPage()&& shouldLoadMore && totalCount > NetworkingManager.PAGE_LIMIT)
                    loadPage(currentPage + 1);
            }
        };
    }

    public void reset() {
        currentPage = 0;
        objects.clear();
    }

    protected View getNoContentView() {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content, null);
        ((TextView) view.findViewById(R.id.no_content_textview)).setText(datasource.noContentString());
        return view;
    }

    public interface PagingAdapterDatasource<T extends BaseModel> {
        void getObjectsAfterObject(T object, NetworkListCallback<T> callback);

        String noContentString();
    }

    public interface PagingAdapaterPageLoadFinishListener<T extends BaseModel> {
        void adapterFinishedLoadingPage(int page);
    }
}

package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import core.networking.NetworkListCallback;
import models.BaseModel;
import models.ServerError;

/**
 * Created by nick on 2/1/14.
 */
public class PagingAdapter<T extends BaseModel> extends BaseAdapter {

    protected ArrayList<T> objects = new ArrayList<T>();
    protected PagingAdapterDatasource datasource;

    public LayoutInflater inflater;
    public ImageLoader imageLoader;

    public interface PagingAdapterDatasource <T extends BaseModel> {
        void getObjectsAfterObject(T object, NetworkListCallback<T> callback);
    }

    public PagingAdapter(LayoutInflater inflater, PagingAdapterDatasource datasource, ImageLoader imageLoader) {
        this.inflater = inflater;
        this.datasource = datasource;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public T getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void loadPage(final int page) {
        T object = objects.size() == 0 ? null : objects.get(objects.size() - 1);

        datasource.getObjectsAfterObject(object, new NetworkListCallback<T>() {
            @Override
            public void completionHandler(ArrayList<T> objectsToAdd, ServerError error) {
                if (error != null || objectsToAdd == null || objectsToAdd.size() == 0)
                    return;

                if (page == 0)
                    objects = objectsToAdd;
                else
                    objects.addAll(objectsToAdd);

                notifyDataSetChanged();
            }
        });
    }

}

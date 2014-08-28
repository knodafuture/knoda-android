package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import java.util.ArrayList;

import models.BaseModel;
import models.Prediction;
import models.ServerError;
import models.User;
import networking.NetworkListCallback;
import views.predictionlists.PredictionListCell;
import views.search.SearchUserCell;

/**
 * Created by nick on 2/11/14.
 */
public class SearchAdapter extends BaseAdapter {

    public ArrayList<ItemEntry> items = new ArrayList<ItemEntry>();
    boolean loading;
    private Context context;
    private ImageLoader imageLoader;
    private SearchAdapterDatasource datasource;
    private SearchAdapterCallbacks callbacks;

    private ArrayList<Prediction> predictions = new ArrayList<Prediction>();
    private ArrayList<User> users = new ArrayList<User>();
    private String searchTerm;

    public SearchAdapter(Context context, SearchAdapterDatasource datasource, SearchAdapterCallbacks callbacks, ImageLoader imageLoader) {
        this.context = context;
        this.imageLoader = imageLoader;
        this.datasource = datasource;
        this.callbacks = callbacks;
        this.loading = true;
    }

    @Override
    public int getCount() {
        if (loading)
            return 1;

        return items.size();
    }

    @Override
    public ItemEntry getItem(int position) {

        if (loading)
            return null;

        return items.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (loading)
            return LayoutInflater.from(context).inflate(R.layout.list_cell_loading, null);


        ItemEntry entry = getItem(position);


        if (entry.type == ItemEntryType.ENTRY_TYPE_HEADER || entry.type == ItemEntryType.ENTRY_TYPE_EMPTY)
            return getTextCell(entry.title);

        if (entry.type == ItemEntryType.ENTRY_TYPE_USER)
            return getUserView((User) entry.model);

        if (entry.type == ItemEntryType.ENTRY_TYPE_PREDICTION)
            return getPredictionView((Prediction) entry.model);

        return null;

    }

    private View getPredictionView(Prediction prediction) {
        PredictionListCell view = new PredictionListCell(context);
        view.setPrediction(prediction);

        if (prediction.userAvatar != null)
            view.avatarImageView.setImageUrl(prediction.userAvatar.small, imageLoader);

        return view;
    }

    private View getUserView(final User user) {
        SearchUserCell view = new SearchUserCell(context);
        view.textView.setText(user.username);

        if (user.avatar != null)
            view.imageView.setImageUrl(user.avatar.small, imageLoader);
        view.follow.setTag(view);
        if (user.following_id != null)
            view.follow.setBackgroundResource(R.drawable.follow_btn_active);
        else
            view.follow.setBackgroundResource(R.drawable.follow_btn);

        view.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onUserFollow(user, v);
            }
        });

        return view;
    }

    private View getTextCell(String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cell_search_section, null);
        TextView textView = (TextView) view.findViewById(R.id.cell_search_header_textview);

        textView.setText(text);
        return view;
    }

    public void loadForSearchTerm(final String searchTerm) {
        if (this.searchTerm == searchTerm)
            return;

        this.searchTerm = searchTerm;
        predictions.clear();
        users.clear();

        datasource.getUsers(searchTerm, new NetworkListCallback<User>() {
            @Override
            public void completionHandler(final ArrayList<User> u, ServerError error) {
                if (error != null)
                    return;

                datasource.getPredictions(searchTerm, new NetworkListCallback<Prediction>() {
                            @Override
                            public void completionHandler(ArrayList<Prediction> p, ServerError error) {
                                if (error != null)
                                    return;

                                predictions = p;
                                users = u;
                                createList();
                                loading = false;
                                notifyDataSetChanged();
                            }
                        }
                );
            }
        });


        notifyDataSetChanged();

        loading = true;
    }

    private void createList() {
        items.clear();

        items.add(new ItemEntry("USERS", ItemEntryType.ENTRY_TYPE_HEADER));

        if (users.size() == 0)
            items.add(new ItemEntry("No users found.", ItemEntryType.ENTRY_TYPE_EMPTY));
        else {
            for (User user : users) {
                items.add(new ItemEntry(user, ItemEntryType.ENTRY_TYPE_USER));
            }
        }

        items.add(new ItemEntry("PREDICTIONS", ItemEntryType.ENTRY_TYPE_HEADER));

        if (predictions.size() == 0) {
            items.add(new ItemEntry("No predictions found.", ItemEntryType.ENTRY_TYPE_EMPTY));
        } else {
            for (Prediction prediction : predictions) {
                items.add(new ItemEntry(prediction, ItemEntryType.ENTRY_TYPE_PREDICTION));
            }
        }
    }

    public AdapterView.OnItemClickListener makeOnItemClickListeners() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemEntry entry = items.get(i);

                if (entry.type == ItemEntryType.ENTRY_TYPE_USER)
                    callbacks.onUserSelected((User) entry.model);
                else if (entry.type == ItemEntryType.ENTRY_TYPE_PREDICTION)
                    callbacks.onPredictionSelected((Prediction) entry.model);
            }
        };
    }

    private enum ItemEntryType {
        ENTRY_TYPE_USER, ENTRY_TYPE_PREDICTION, ENTRY_TYPE_EMPTY, ENTRY_TYPE_HEADER
    }

    public interface SearchAdapterDatasource {
        void getUsers(String searchTerm, NetworkListCallback<User> callback);

        void getPredictions(String searchTerm, NetworkListCallback<Prediction> callback);
    }

    public interface SearchAdapterCallbacks {
        void onUserSelected(User user);

        void onUserFollow(User user, View v);

        void onPredictionSelected(Prediction prediciton);
    }

    private class ItemEntry {

        public String title;
        public ItemEntryType type;
        public BaseModel model;

        public ItemEntry(String title, ItemEntryType type) {
            this.title = title;
            this.type = type;
        }

        public ItemEntry(BaseModel model, ItemEntryType type) {
            this.model = model;
            this.type = type;
        }

    }
}

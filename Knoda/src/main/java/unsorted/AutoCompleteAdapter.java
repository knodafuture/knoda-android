package unsorted;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import models.ServerError;
import models.User;
import networking.NetworkListCallback;
import networking.NetworkListObjectCallback;
import views.core.MainActivity;

/**
 * Created by jeffcailteux on 10/6/14.
 */
public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    public String allwords = "";
    private ArrayList<String> mData;
    private MainActivity mainActivity;

    public AutoCompleteAdapter(Context context, int textViewResourceId, MainActivity mainActivity) {
        super(context, textViewResourceId);
        mData = new ArrayList<String>();
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int index) {
        return mData.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint == null || constraint.length() < 1)
                    return new FilterResults();
                allwords = constraint.toString();
                String[] words = constraint.toString().split(" ");
                String lastword = words[words.length - 1];
                //constraint is the text typed into the autocomplete text view
                final FilterResults filterResults = new FilterResults();
                if (constraint != null) {

                    if (lastword.charAt(0) == '#') {
                        mainActivity.networkingManager.autocompleteHashtag(lastword.substring(1), new NetworkListObjectCallback<String>() {
                            @Override
                            public void completionHandler(ArrayList<String> object, ServerError error) {
                                if (error == null && object != null) {
                                    mData.clear();
//                                    ArrayList<String> hashtags = new ArrayList<String>();
//                                    for (String h : object) {
//                                        hashtags.add(h);
//                                    }
                                    mData = object;
                                    filterResults.values = mData;
                                    filterResults.count = mData.size();
                                    if (mData.size() > 0) {
                                        notifyDataSetChanged();
                                    } else {
                                        notifyDataSetInvalidated();
                                    }
                                }
                            }
                        });
                    } else if (lastword.charAt(0) == '@') {
                        mainActivity.networkingManager.autoCompleteUsers(lastword.substring(1), new NetworkListCallback<User>() {
                            @Override
                            public void completionHandler(ArrayList<User> object, ServerError error) {
                                if (error == null && object != null) {
                                    mData.clear();
                                    ArrayList<String> usernames = new ArrayList<String>();
                                    for (User u : object) {
                                        usernames.add(u.username);
                                    }
                                    mData = usernames;
                                    filterResults.values = mData;
                                    filterResults.count = mData.size();
                                    if (mData.size() > 0) {
                                        notifyDataSetChanged();
                                    } else {
                                        notifyDataSetInvalidated();
                                    }
                                }
                            }
                        });
                    } else
                        return filterResults;

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }
}



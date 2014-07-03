package adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import helpers.AdapterHelper;
import models.ServerError;
import models.User;
import networking.NetworkListCallback;
import views.details.TallyCell;
import views.details.TallyHeaderCell;

/**
 * Created by nick on 2/18/14.
 */
public class TallyAdapter extends DetailsAdapter<User> {

    private ArrayList<User> agreedUsers;
    private ArrayList<User> disagreedUsers;
    private TallyAdapterDatasource tallyDatasource;
    private TallyAdapterDelegate delegate;

    public TallyAdapter(Context context, TallyAdapterDatasource datasource, TallyAdapterDelegate delegate) {
        super(context, null, null);
        this.tallyDatasource = datasource;
        this.delegate = delegate;
    }

    @Override
    public int getCount() {
        if (objects.size() == 0)
            return super.getCount();

        return super.getCount() + 1;
    }

    public void loadPage(final int page) {

        if (loading || page != 0)
            return;

        loading = true;

        tallyDatasource.getAgreedUsers(new NetworkListCallback<User>() {
            @Override
            public void completionHandler(final ArrayList<User> agreed, ServerError error) {
                if (error != null || agreed == null)
                    return;

                tallyDatasource.getDisagreedUsers(new NetworkListCallback<User>() {
                    @Override
                    public void completionHandler(ArrayList<User> disagreed, ServerError error) {
                        if (error != null || disagreed == null)
                            return;

                        agreedUsers = agreed;
                        disagreedUsers = disagreed;

                        objects = agreedUsers.size() > disagreedUsers.size() ? agreedUsers : disagreedUsers;

                        currentPage = page;
                        loading = false;
                        notifyDataSetChanged();

                        if (onLoadFinished != null)
                            onLoadFinished.adapterFinishedLoadingPage(page);
                    }
                });
            }
        });
    }

    @Override
    public boolean canLoadNextPage() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        position = transformPosition(position);

        if (position >= objects.size() + 1 || objects.size() == 0 || position < 0)
            return super.getView(position, convertView, parent);

        if (position == 0) {
            TallyHeaderCell header = new TallyHeaderCell(context);
            header.setBackgroundColor(Color.WHITE);
            header.agreedTextView.setText("Agreed " + agreedUsers.size());
            header.disagreedTextView.setText("Disagreed " + disagreedUsers.size());
            return header;
        }

        position = position - 1;

        User agreeduser = position >= agreedUsers.size() ? null : agreedUsers.get(position);
        User disagreedUser = position >= disagreedUsers.size() ? null : disagreedUsers.get(position);

        TallyCell listItem = (TallyCell) AdapterHelper.getConvertViewSafely(convertView, TallyCell.class);

        if (listItem == null)
            listItem = new TallyCell(context);
        listItem.setBackgroundColor(Color.WHITE);
        if (agreeduser != null) {

            if (agreeduser.verified)
                listItem.leftCheckmark.setVisibility(View.VISIBLE);
            else
                listItem.leftCheckmark.setVisibility(View.INVISIBLE);

            listItem.leftTextView.setText(agreeduser.username);
            listItem.leftTextView.setOnClickListener(getClickListener(agreeduser));
        } else {
            listItem.leftCheckmark.setVisibility(View.INVISIBLE);
            listItem.leftTextView.setText("");
            listItem.leftTextView.setOnClickListener(null);
        }
        if (disagreedUser != null) {
            if (disagreedUser.verified)
                listItem.rightCheckmark.setVisibility(View.VISIBLE);
            else
                listItem.rightCheckmark.setVisibility(View.INVISIBLE);
            listItem.rightTextView.setText(disagreedUser.username);
            listItem.rightTextView.setOnClickListener(getClickListener(disagreedUser));
        } else {
            listItem.rightCheckmark.setVisibility(View.INVISIBLE);
            listItem.rightTextView.setText("");
            listItem.rightTextView.setOnClickListener(null);
        }
        return listItem;
    }

    private View.OnClickListener getClickListener(final User user) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onUserClicked(user);
            }
        };
    }

    public interface TallyAdapterDatasource {
        void getAgreedUsers(NetworkListCallback<User> callback);

        void getDisagreedUsers(NetworkListCallback<User> callback);
    }

    public interface TallyAdapterDelegate {
        void onUserClicked(User user);
    }


}

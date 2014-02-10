package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;

import helpers.AdapterHelper;
import models.Prediction;
import models.User;
import views.profile.UserProfileHeaderView;

/**
 * Created by nick on 2/3/14.
 */
public class AnotherUsersProfileAdapter extends PredictionAdapter {


    public User user;

    public AnotherUsersProfileAdapter(LayoutInflater inflater, PagingAdapterDatasource<Prediction> datasource, ImageLoader imageLoader) {
        super(inflater, datasource, imageLoader);
    }

    @Override
    public int getCount() {
        if (user == null)
            return super.getCount();

        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (user == null)
            return super.getView(position, convertView, parent);

        if (position == 0)
            return getHeaderView(convertView);

        return super.getView(position - 1, convertView, parent);

    }

    public void setUser(User user) {
        this.user = user;
        notifyDataSetChanged();
    }

    View getHeaderView(View convertView) {

        UserProfileHeaderView header = (UserProfileHeaderView) AdapterHelper.getConvertViewSafely(convertView, UserProfileHeaderView.class);

        if (header == null)
            header = new UserProfileHeaderView(inflater.getContext(), null);

        header.pointsTextView.setText(user.points.toString());

        header.winLossTextView.setText(user.won.toString() + "-" + user.lost.toString());

        header.streakTextView.setText(user.streak.toString());
        header.winPercentTextView.setText(user.winningPercentage.toString());

        header.avatarImageView.setImageUrl(user.avatar.big, imageLoader);

        return header;
    }
}

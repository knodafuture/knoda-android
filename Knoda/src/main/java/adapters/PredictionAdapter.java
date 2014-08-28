package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import helpers.AdapterHelper;
import managers.SharedPrefManager;
import models.Prediction;
import pubsub.NewPredictionEvent;
import pubsub.PredictionChangeEvent;
import views.predictionlists.HomeFragment;
import views.predictionlists.PredictionListCell;

public class PredictionAdapter extends PagingAdapter<Prediction> {

    public Bus bus;
    public SharedPrefManager sharedPrefManager;
    public boolean showContestTour = false;
    boolean disableTour = false;
    HomeFragment homeFragment = null;

    public PredictionAdapter(Context context, PagingAdapterDatasource<Prediction> datasource, ImageLoader imageLoader, Bus bus, HomeFragment fragment) {
        super(context, datasource, imageLoader);
        this.bus = bus;
        this.sharedPrefManager = new SharedPrefManager(context);
        this.bus.register(this);
        this.homeFragment = fragment;
    }

    public PredictionAdapter(Context context, PagingAdapterDatasource<Prediction> datasource, ImageLoader imageLoader, Bus bus, boolean disableTour) {
        super(context, datasource, imageLoader);
        this.bus = bus;
        this.sharedPrefManager = new SharedPrefManager(context);
        this.bus.register(this);
        this.disableTour = disableTour;
    }

    @Subscribe
    public void newPrediction(NewPredictionEvent event) {
        insertAt(event.prediction, 0);
    }

    @Subscribe
    public void changedPrediction(PredictionChangeEvent event) {
        updatePrediction(event.prediction);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);

        PredictionListCell listItem = (PredictionListCell) AdapterHelper.getConvertViewSafely(convertView, PredictionListCell.class);
        if (listItem == null)
            listItem = new PredictionListCell(context);

        Prediction prediction = getItem(position);
        listItem.setPrediction(prediction);
        listItem.setTag(position);
        if (prediction.userAvatar != null)
            listItem.avatarImageView.setImageUrl(prediction.userAvatar.small, imageLoader);

        if (sharedPrefManager.getFirstLaunch() && sharedPrefManager.shouldShowVotingWalkthrough() && position == 0 && !disableTour) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.view_swipe_walkthrough, null);
            if (listItem.walkthroughView.getChildCount() == 0) {
                parent.setTag(v);
                listItem.walkthroughView.addView(v);
            }
        } else {
            listItem.walkthroughView.removeAllViews();
        }

        return listItem;
    }

    private void updatePrediction(Prediction prediction) {
        for (int i = 0; i < objects.size(); i++) {
            Prediction p = objects.get(i);
            if (p.id.equals(prediction.id)) {
                objects.set(i, prediction);
                return;
            }
        }
    }

    @Override
    protected View getNoContentView() {
        if (homeFragment == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content, null);
            ((TextView) view.findViewById(R.id.no_content_textview)).setText(datasource.noContentString());
            return view;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content_follow_feed, null);
            return view;
        }
    }
}

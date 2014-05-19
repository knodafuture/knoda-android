package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import helpers.AdapterHelper;
import models.Prediction;
import pubsub.NewPredictionEvent;
import pubsub.PredictionChangeEvent;
import views.predictionlists.PredictionListCell;

public class PredictionAdapter extends PagingAdapter<Prediction> {

    public Bus bus;

    public PredictionAdapter(Context context, PagingAdapterDatasource<Prediction> datasource, ImageLoader imageLoader, Bus bus) {
        super(context, datasource, imageLoader);
        this.bus = bus;
        this.bus.register(this);
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
        if (prediction.userAvatar != null)
            listItem.avatarImageView.setImageUrl(prediction.userAvatar.small, imageLoader);

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
}

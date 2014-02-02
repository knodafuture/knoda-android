package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import models.Prediction;
import views.predictionlists.PredictionListCell;

/**
 * Created by nick on 1/27/14.
 */
public class HomeAdapter extends PagingAdapter<Prediction> {

    public HomeAdapter(LayoutInflater inflater, PagingAdapterDatasource<Prediction> datasource, ImageLoader imageLoader) {
        super(inflater, datasource, imageLoader);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PredictionListCell listItem = (PredictionListCell) convertView;
        if (listItem == null)
            listItem = (PredictionListCell) inflater.inflate(R.layout.list_cell_predictions, null);

        Prediction prediction = getItem(position);

        listItem.setPrediction(prediction);
        if (prediction.userAvatar != null)
            listItem.avatarImageView.setImageUrl(prediction.userAvatar.small, imageLoader);

        return listItem;
    }



}

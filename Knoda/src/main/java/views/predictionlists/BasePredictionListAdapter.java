package views.predictionlists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import java.util.ArrayList;

import models.Prediction;

/**
 * Created by nick on 1/27/14.
 */
public class BasePredictionListAdapter extends BaseAdapter {

    private ArrayList<Prediction> predictions;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    public BasePredictionListAdapter(LayoutInflater inflater, ArrayList<Prediction> predictions, ImageLoader imageLoader) {
        this.inflater = inflater;
        this.predictions = predictions;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return predictions.size();
    }

    @Override
    public Prediction getItem(int position) {
        return predictions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return predictions.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PredictionListCell listItem = (PredictionListCell) convertView;
        if (listItem == null)
            listItem = (PredictionListCell) inflater.inflate(R.layout.list_cell_predictions, null);

        Prediction prediction = getItem(position);

        listItem.bodyTextView.setText(prediction.body);
        listItem.usernameTextView.setText(prediction.username);
        listItem.timeStampsTextView.setText(prediction.getMetdataString());
        if (prediction.userAvatar != null)
            listItem.avatarImageView.setImageUrl(prediction.userAvatar.small, imageLoader);
        return listItem;
    }



}

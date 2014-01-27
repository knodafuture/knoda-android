package views.predictionlists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.knoda.knoda.R;

import java.util.ArrayList;

import models.Prediction;

/**
 * Created by nick on 1/27/14.
 */
public class BasePredictionListAdapter extends BaseAdapter {

    private ArrayList<Prediction> predictions;
    private LayoutInflater inflater;

    public BasePredictionListAdapter(LayoutInflater inflater, ArrayList<Prediction> predictions) {
        this.inflater = inflater;
        this.predictions = predictions;
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
            listItem = (PredictionListCell) inflater.inflate(R.layout.list_predictions, null);

        Prediction prediction = getItem(position);

        listItem.bodyTextView.setText(prediction.body);
        listItem.usernameTextView.setText(prediction.username);

        return listItem;
    }


}

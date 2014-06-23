package views.predictionlists;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;

import adapters.PagingAdapter;
import adapters.PredictionAdapter;
import listeners.PredictionSwipeListener;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.PredictionChangeEvent;
import views.core.BaseListFragment;
import views.core.MainActivity;
import views.details.DetailsFragment;

/**
 * Created by nick on 2/3/14.
 */
public class BasePredictionListFragment extends BaseListFragment implements PredictionSwipeListener.PredictionCellCallbacks, PagingAdapter.PagingAdapterDatasource<Prediction> {

    PredictionSwipeListener swipeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public PagingAdapter getAdapter() {
        return new PredictionAdapter(getActivity(), this, networkingManager.getImageLoader(), bus);
    }

    @Override
    public AbsListView.OnScrollListener getOnScrollListener() {
        return swipeListener.makeScrollListener();
    }

    public void refreshList(){
        adapter.loadPage(0);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        swipeListener = new PredictionSwipeListener(listView, this);
        listView.setOnTouchListener(swipeListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked(i);
            }
        });
    }

    public void onItemClicked(int position) {
        Prediction prediction = (Prediction) adapter.getItem(position - 1);
        if (prediction != null) {
            DetailsFragment fragment = DetailsFragment.newInstance(prediction);
            pushFragment(fragment);
        }
    }

    @Override
    public void getObjectsAfterObject(Prediction object, final NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getPredictionsAfter(lastId, callback);

    }



    @Override
    public void onPredictionAgreed(final PredictionListCell cell) {
        cell.setAgree(true);

        networkingManager.agreeWithPrediction(cell.prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                if (error != null)
                    errorReporter.showError(error);
                else {
                    cell.prediction = object;
                    cell.update();
                    bus.post(new PredictionChangeEvent(object));
                    ((MainActivity) getActivity()).checkBadges();
                }
            }
        });
        FlurryAgent.logEvent("Swiped_Agree");
    }

    @Override
    public void onPredictionDisagreed(final PredictionListCell cell) {
        cell.setAgree(false);

        networkingManager.disagreeWithPrediction(cell.prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                if (error != null) {
                    errorReporter.showError(error);
                } else {
                    cell.prediction = object;
                    cell.update();
                    bus.post(new PredictionChangeEvent(object));
                    ((MainActivity) getActivity()).checkBadges();
                }
            }
        });
        FlurryAgent.logEvent("Swiped_Disagree");
    }

    @Override
    public String noContentString() {
        return "";
    }
}

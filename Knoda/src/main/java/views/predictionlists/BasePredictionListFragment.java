package views.predictionlists;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import adapters.PagingAdapter;
import adapters.PredictionAdapter;
import listeners.PredictionSwipeListener;
import models.Challenge;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import unsorted.Logger;
import views.core.BaseListFragment;

/**
 * Created by nick on 2/3/14.
 */
public class BasePredictionListFragment extends BaseListFragment implements PredictionSwipeListener.PredictionCellCallbacks, PagingAdapter.PagingAdapterDatasource<Prediction> {


    PredictionSwipeListener swipeListener;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public PagingAdapter getAdapter() {
        return new PredictionAdapter(getActivity().getLayoutInflater(), this, networkingManager.getImageLoader());
    }

    @Override
    public AbsListView.OnScrollListener getOnScrollListener() {
        return swipeListener.makeScrollListener();
    }

    @Override
    public void onListViewCreated(ListView listView1) {
        swipeListener = new PredictionSwipeListener(listView, this);
        listView.setOnTouchListener(swipeListener);
    }


    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getPredictionsAfter(lastId, callback);
    }


    @Override
    public void onPredictionAgreed(final PredictionListCell cell) {
        cell.setAgree(true);

        networkingManager.agreeWithPrediction(cell.prediction.id, new NetworkCallback<Challenge>() {
            @Override
            public void completionHandler(Challenge object, ServerError error) {
                if (error != null)
                    errorReporter.showError(error);
                else {
                    cell.prediction.challenge = object;
                    cell.update();
                }
            }
        });
    }

    @Override
    public void onPredictionDisagreed(final PredictionListCell cell) {
        cell.setAgree(false);

        networkingManager.disagreeWithPrediction(cell.prediction.id, new NetworkCallback<Challenge>() {
            @Override
            public void completionHandler(Challenge object, ServerError error) {
                if (error != null) {
                    errorReporter.showError(error);
                } else {
                    cell.prediction.challenge = object;
                    cell.update();
                }
            }
        });
    }

    @Override
    public void onProfileTapped(final PredictionListCell cell) {
        if (cell.prediction.userId == userManager.getUser().id) {
            Logger.log("current user profile unimplemented");
        } else {
            AnotherUsersProfileFragment fragment = new AnotherUsersProfileFragment(cell.prediction.userId);
            pushFragment(fragment);
        }
    }
}

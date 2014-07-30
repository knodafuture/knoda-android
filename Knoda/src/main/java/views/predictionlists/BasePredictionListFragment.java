package views.predictionlists;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    public void refreshList() {
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
                }
            }
        });
        guestContest(cell.prediction);
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
                }
            }
        });
        guestContest(cell.prediction);
        FlurryAgent.logEvent("Swiped_Disagree");
    }

    private void guestContest(Prediction prediction) {
        if (prediction.contest_id == null)
            return;
        if (userManager.getUser() == null || userManager.getUser().guestMode) {
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
            b.setTitle("Show me the contest!")
                    .setMessage("This prediction is part of a contest. To be eligible to win, you must be a registered Knoda user. Sign up now for your chance to win!")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) getActivity()).showLogin("Giddy Up!", "Now we're talking! Choose an option below to sign-up and start participating in contests.");
                        }
                    })
                    .show();
        }
    }

    @Override
    public String noContentString() {
        return "";
    }
}

package views.details;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

import adapters.CommentAdapter;
import adapters.PagingAdapter;
import adapters.TallyAdapter;
import models.Challenge;
import models.Comment;
import models.Prediction;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import views.core.BaseListFragment;
import views.predictionlists.AnotherUsersProfileFragment;
import views.predictionlists.CategoryFragment;

/**
 * Created by nick on 2/13/14.
 */
public class DetailsFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<Comment>, TallyAdapter.TallyAdapterDatasource,
        TallyAdapter.TallyAdapterDelegate, DetailsActionbar.DetailsActionBarDelegate,
        CommentAdapter.CommentAdapterDelegate, DetailsHeaderView.DetailsHeaderViewDelegate {

    private CommentAdapter commentAdapter;
    private TallyAdapter tallyAdapter;
    private DetailsHeaderView headerview;
    private DetailsActionbar actionbar;

    private Prediction prediction;


    public DetailsFragment(Prediction prediction) {
        this.prediction = prediction;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setTitle("Details");

    }

    @Override
    public PagingAdapter getAdapter() {
        return commentAdapter;
    }

    @Override
    public void onListViewCreated(ListView listView) {
        commentAdapter = new CommentAdapter(getActivity().getLayoutInflater(), this, this, networkingManager.getImageLoader());
        tallyAdapter = new TallyAdapter(getActivity().getLayoutInflater(), this, this);

        headerview = new DetailsHeaderView(getActivity());
        headerview.setPrediction(prediction);

        headerview.predictionCell.avatarImageView.setImageUrl(prediction.userAvatar.small, networkingManager.getImageLoader());
        actionbar = new DetailsActionbar(getActivity(), this);

        commentAdapter.setActionBar(actionbar);
        commentAdapter.setHeader(headerview);

        tallyAdapter.setActionBar(actionbar);
        tallyAdapter.setHeader(headerview);

        tallyAdapter.loadPage(0);
    }

    @Override
    public void getObjectsAfterObject(Comment comment, final NetworkListCallback<Comment> callback) {

        Integer lastId = comment == null ? 0 : comment.id;

        networkingManager.getCommentsForPredictionWithLastId(this.prediction.id, lastId, new NetworkListCallback<Comment>() {
            @Override
            public void completionHandler(ArrayList<Comment> object, ServerError error) {
                callback.completionHandler(object, error);
            }
        });
    }

    @Override
    public void getAgreedUsers(NetworkListCallback<User> callback) {
        networkingManager.getAgreedUsers(prediction.id, callback);
    }

    @Override
    public void getDisagreedUsers(NetworkListCallback<User> callback) {
        networkingManager.getDisagreedUsers(prediction.id, callback);
    }

    @Override
    public void onUserClicked(User user) {
        AnotherUsersProfileFragment fragment = new AnotherUsersProfileFragment(user.userId);
        pushFragment(fragment);
    }

    @Override
    public void onUserClicked(Integer userId) {
        AnotherUsersProfileFragment fragment = new AnotherUsersProfileFragment(userId);
        pushFragment(fragment);
    }

    @Override
    public void onComments() {
        listView.setAdapter(commentAdapter);
    }

    @Override
    public void onTally() {
        listView.setAdapter(tallyAdapter);
    }

    @Override
    public void onSimilar() {
        CategoryFragment fragment = new CategoryFragment(prediction.tags.get(0));
        pushFragment(fragment);
    }

    @Override
    public void onShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, prediction.body);
        startActivity(Intent.createChooser(share, "How would you like to share?"));
    }

    @Override
    public void onAgree() {
        spinner.show();

        networkingManager.agreeWithPrediction(prediction.id, new NetworkCallback<Challenge>() {
            @Override
            public void completionHandler(Challenge object, ServerError error) {
                spinner.hide();
                if (error != null)
                    return;

                prediction.challenge = object;
                headerview.setPrediction(prediction);
            }
        });
    }

    @Override
    public void onDisagree() {
        spinner.show();

        networkingManager.disagreeWithPrediction(prediction.id, new NetworkCallback<Challenge>() {
            @Override
            public void completionHandler(Challenge object, ServerError error) {
                spinner.hide();

                if (error != null)
                    return;

                prediction.challenge = object;
                headerview.setPrediction(prediction);
            }
        });

    }

    @Override
    public void onBS() {
        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .setMessage("Don't be lame. Tell the truth. It's more fun this way. Is this really the wrong outcome?")
                .create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(sendBS());
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

    private View.OnClickListener sendBS() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.show();

                networkingManager.sendBS(prediction.id, new NetworkCallback<Prediction>() {
                    @Override
                    public void completionHandler(Prediction object, ServerError error) {
                        spinner.hide();

                        if (error != null)
                            return;

                        prediction = object;
                        headerview.setPrediction(prediction);
                    }
                });
            }
        };

    }

    @Override
    public void onOutcome(boolean outcome) {
        spinner.show();

        networkingManager.sendOutcome(prediction.id, outcome, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                spinner.hide();

                if (error != null)
                    return;
                prediction = object;
                headerview.setPrediction(prediction);
            }
        });
    }

    @Override
    public void onIDK() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), updateResolutionDate(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setTitle("When will you know?");
        dialog.show();

    }

    private DatePickerDialog.OnDateSetListener updateResolutionDate() {

        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Prediction update = new Prediction();
                update.id = prediction.id;

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                update.resolutionDate = new DateTime(calendar.getTime());

                spinner.show();

                networkingManager.updatePrediction(prediction, new NetworkCallback<Prediction>() {
                    @Override
                    public void completionHandler(Prediction object, ServerError error) {
                        spinner.show();

                        if (error != null)
                            errorReporter.showError(error);
                        else {
                            prediction = object;
                            headerview.setPrediction(prediction);
                        }
                    }
                });
            }
        };


    }
}

package views.predictionlists;

import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;

import adapters.AnotherUsersProfileAdapter;
import adapters.PagingAdapter;
import models.Prediction;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import views.profile.PhotoFragment;

/**
 * Created by nick on 2/3/14.
 */
public class AnotherUsersProfileFragment extends BasePredictionListFragment {

    private Integer userId;
    private User user;

    public AnotherUsersProfileFragment(Integer userId) {
        super();
        this.userId = userId;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle("");


        networkingManager.getUser(userId, new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                if (error != null || object == null)
                    errorReporter.showError(error);
                else {
                    user = object;
                    setTitle(object.username.toUpperCase());
                    ((AnotherUsersProfileAdapter)adapter).setUser(object);
                }
            }
        });
        FlurryAgent.logEvent("Another_User_Profile_Screen");
    }

    @Override
    public PagingAdapter getAdapter() {
        return new AnotherUsersProfileAdapter(getActivity(), this, networkingManager.getImageLoader());
    }

    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getPredictionsForUserAfter(userId, lastId, callback);
    }

    @Override
    public void onProfileTapped(final PredictionListCell cell) {
    }

    @Override
    public void onItemClicked(int position) {
        position = position - 1;
        if (position <= 0) {
            if (user.avatar != null) {
                PhotoFragment fragment = new PhotoFragment(user.avatar.big);
                pushFragment(fragment);
            }
            return;
        }

        super.onItemClicked(position);
    }



}

package views.predictionlists;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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

public class AnotherUsersProfileFragment extends BasePredictionListFragment {

    private Integer userId;
    private User user;

    public static AnotherUsersProfileFragment newInstance(Integer userId) {
        AnotherUsersProfileFragment fragment = new AnotherUsersProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("USER_ID", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getInt("USER_ID");
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        networkingManager.getUser(userId, new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                if (error != null || object == null)
                    errorReporter.showError(error);
                else {
                    user = object;
                    setTitle(object.username.toUpperCase());
                    ((AnotherUsersProfileAdapter) adapter).setUser(object);
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
    public void onItemClicked(int position) {
//        position = position - 1;
//        if (position <= 0) {
//            if (user.avatar != null) {
//                PhotoFragment fragment = PhotoFragment.newInstance(user.avatar.big);
//                pushFragment(fragment);
//            }
//            return;
//        }

        super.onItemClicked(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }
}

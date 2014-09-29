package views.predictionlists;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import adapters.AnotherUsersProfileAdapter;
import adapters.PagingAdapter;
import models.Follow;
import models.FollowUser;
import models.Prediction;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import views.core.MainActivity;

public class AnotherUsersProfileFragment extends BasePredictionListFragment implements FollowButton.FollowButtonCallbacks {

    private Integer userId;
    private User user;
    private Menu menu;
    private FollowButton followButton;

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

                    ((AnotherUsersProfileAdapter) adapter).setUser(object);
                    if (menu != null && menu.findItem(R.id.action_follow) != null && menu.findItem(R.id.action_follow).getActionView() != null) {
                        if (user.following_id != null)
                            menu.findItem(R.id.action_follow).getActionView().findViewById(R.id.view_follow_button).setBackgroundResource(R.drawable.follow_btn_active);
                        else
                            menu.findItem(R.id.action_follow).getActionView().findViewById(R.id.view_follow_button).setBackgroundResource(R.drawable.follow_btn);

                        setTitle(object.username.toUpperCase());
                    }

                }
            }
        });
        FlurryAgent.logEvent("Another_User_Profile_Screen");
    }

    @Override
    public void onItemClicked(int position) {
        position--;
        if (position <= 0) {
            return;
        }

        super.onItemClicked(position);
    }

    @Override
    public PagingAdapter getAdapter() {
        return new AnotherUsersProfileAdapter(getActivity(), this, networkingManager.getImageLoader(), ((MainActivity) getActivity()));
    }

    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getPredictionsForUserAfter(userId, lastId, callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.anotherprofile, menu);
        followButton = (FollowButton) menu.findItem(R.id.action_follow).getActionView();
        followButton.setCallbacks(this);
        this.menu = menu;
    }

    @Override
    public void onFollowClick() {
        FollowUser followUser = new FollowUser();
        followUser.leader_id = user.id;

        if (user.following_id != null) {
            //unfollow
            spinner.show();
            networkingManager.unfollowUser(user.following_id, new NetworkCallback<FollowUser>() {
                @Override
                public void completionHandler(FollowUser object, ServerError error) {
                    spinner.hide();
                    if (user != null) {
                        user.following_id = null;
                        user.follower_count--;
                        userManager.getUser().following_count--;
                        ((AnotherUsersProfileAdapter) adapter).setUser(user);
                    }
                    if (menu != null)
                        menu.findItem(R.id.action_follow).getActionView().findViewById(R.id.view_follow_button).setBackgroundResource(R.drawable.follow_btn);
                }
            });
        } else {
            //follow
            spinner.show();
            networkingManager.followUser(followUser, new NetworkCallback<Follow>() {
                @Override
                public void completionHandler(Follow object, ServerError error) {
                    spinner.hide();
                    if (user != null) {
                        user.following_id = object.id;
                        user.follower_count++;
                        userManager.getUser().following_count++;
                        ((AnotherUsersProfileAdapter) adapter).setUser(user);
                    }
                    if (menu != null)
                        menu.findItem(R.id.action_follow).getActionView().findViewById(R.id.view_follow_button).setBackgroundResource(R.drawable.follow_btn_active);

                }
            });

        }
    }

    @Override
    public String noContentString() {
        return "No Predictions";
    }

}

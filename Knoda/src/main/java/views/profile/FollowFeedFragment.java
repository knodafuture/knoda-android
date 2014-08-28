package views.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import adapters.FollowUserAdapter;
import adapters.PagingAdapter;
import models.Follow;
import models.FollowUser;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import views.core.BaseListFragment;

public class FollowFeedFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<User> {
    int screenNumber;
    User user;

    public FollowFeedFragment() {
    }

    public static FollowFeedFragment newInstance(int position, User user) {
        FollowFeedFragment fragment = new FollowFeedFragment();
        fragment.user = user;
        fragment.screenNumber = position;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("Follow Feed" + screenNumber);
    }

    @Override
    public PagingAdapter getAdapter() {
        return new FollowUserAdapter(getActivity(), this, networkingManager.getImageLoader(), this);
    }


    @Override
    public void getObjectsAfterObject(User object, final NetworkListCallback<User> callback) {
        boolean f = (screenNumber == 1) ? false : true;
        boolean needAuth = user.userId == userManager.getUser().userId;
        networkingManager.getFollow(user, f, callback);
    }

    @Override
    public String noContentString() {
        if (screenNumber == 0)
            return "No Followers";
        else
            return "No Following";
    }


    public void followUser(final User user, final Button followBtn, final View cover) {
        if (user.following_id == null) {
            FollowUser followUser = new FollowUser();
            followUser.leader_id = user.id;
            networkingManager.followUser(followUser, new NetworkCallback<Follow>() {
                @Override
                public void completionHandler(Follow object, ServerError error) {
                    userManager.getUser().following_count++;
                    user.following_id = object.id;
                    followBtn.setEnabled(true);
                    cover.setVisibility(View.GONE);
                    followBtn.setBackgroundResource(R.drawable.follow_btn_active);
                }
            });
        } else {
            networkingManager.unfollowUser(user.following_id, new NetworkCallback<FollowUser>() {
                @Override
                public void completionHandler(FollowUser object, ServerError error) {
                    userManager.getUser().following_count--;
                    user.following_id = null;
                    followBtn.setEnabled(true);
                    cover.setVisibility(View.GONE);
                    followBtn.setBackgroundResource(R.drawable.follow_btn);
                }
            });
        }
    }

}
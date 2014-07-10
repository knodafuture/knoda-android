package views.predictionlists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import views.core.BaseFragment;

public class AnotherUsersProfile2Fragment extends BaseFragment {

    private Integer userId;
    private User user;

    @InjectView(R.id.topview)
    LinearLayout topview;

    @InjectView(R.id.profile_avatar)
    NetworkImageView avatarIcon;
    @InjectView(R.id.profile_points)
    TextView tv_points;
    @InjectView(R.id.profile_winpercent)
    TextView tv_winpercent;
    @InjectView(R.id.profile_winstreak)
    TextView tv_winstreak;
    @InjectView(R.id.profile_winloss)
    TextView tv_winloss;
    TextView selectedFilter;
    View selectedUnderline;
    @InjectView(R.id.base_listview)
    PullToRefreshListView pListView;


    public static AnotherUsersProfile2Fragment newInstance(Integer userId) {
        AnotherUsersProfile2Fragment fragment = new AnotherUsersProfile2Fragment();
        Bundle bundle = new Bundle();
        bundle.putInt("USER_ID", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getInt("USER_ID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myprofile2, container, false);
        ButterKnife.inject(this, view);
        getActivity().invalidateOptionsMenu();

        selectedFilter = (TextView) view.findViewById(R.id.activity_1);
        selectedUnderline = view.findViewById(R.id.underline_1);

        return view;
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
                    updateUser(user);
                }
            }
        });
        FlurryAgent.logEvent("Another_User_Profile_Screen");
    }

    private void updateUser(User user) {
        if (user == null)
            return;

        setTitle(user.username.toUpperCase());

        if (user.avatar != null)
            avatarIcon.setImageUrl(user.avatar.big, networkingManager.getImageLoader());

        tv_points.setText(user.points.toString());
        tv_winstreak.setText(user.streak.toString());
        tv_winpercent.setText(user.winningPercentage.toString() + "%");
        tv_winloss.setText(user.won.toString() + "-" + user.lost.toString());

    }
}

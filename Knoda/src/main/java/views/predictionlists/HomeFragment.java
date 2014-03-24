package views.predictionlists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import models.Group;
import models.ServerError;
import networking.NetworkListCallback;
import views.core.MainActivity;

public class HomeFragment extends BasePredictionListFragment {

    @InjectView(R.id.home_overlay)
    RelativeLayout overlay;

    @InjectView(R.id.over_button)
    Button overlayButton;

    @OnClick(R.id.home_overlay) void onClick() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
    public HomeFragment() {}

    @Override public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (overlay.getVisibility() == View.GONE)
            return super.onOptionsItemSelected(item);

        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle("HOME");
        FlurryAgent.logEvent("Home_Screen");
        handleOverlay();
    }


    private void handleOverlay() {
        networkingManager.getGroups(new NetworkListCallback<Group>() {
            @Override
            public void completionHandler(ArrayList<Group> object, ServerError error) {
                List<Group> groups = object;
            }
        });
        boolean firstLaunch = sharedPrefManager.getFirstLaunch();

        if (firstLaunch) {
            FlurryAgent.logEvent("First_Screen_Overlay");
            overlay.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).setActionBarEnabled(false);
            sharedPrefManager.setFirstLaunch(false);
            overlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).setActionBarEnabled(true);
                    overlay.setVisibility(view.GONE);
                    overlayButton.setOnClickListener(null);
                }
            });

        }
        else
            overlay.setVisibility(View.GONE);
    }
}

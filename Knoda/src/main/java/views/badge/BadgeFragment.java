package views.badge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.ArrayList;

import adapters.BadgeAdapter;
import models.Badge;
import models.ServerError;
import networking.NetworkListCallback;
import views.core.BaseFragment;

/**
 * Created by adamengland on 2/14/14.
 */
public class BadgeFragment extends BaseFragment {
    public static BadgeFragment newInstance() {
        BadgeFragment fragment = new BadgeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_badge, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final GridView gridview = (GridView) getActivity().findViewById(R.id.grid);
        final BadgeAdapter adapter = new BadgeAdapter(getActivity(), networkingManager.getImageLoader());
        gridview.setAdapter(adapter);
        networkingManager.getBadges(new NetworkListCallback<Badge>() {
            @Override
            public void completionHandler(ArrayList<Badge> object, ServerError error) {
                if (error == null) {
                    adapter.addAll(object);
                } else {
                    errorReporter.showError(error);
                }
            }
        });
        FlurryAgent.logEvent("Badges_Screen");
    }
}

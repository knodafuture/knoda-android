package views.core;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import butterknife.ButterKnife;
import core.ErrorReporter;
import networking.NetworkingManager;

/**
 * Created by nick on 1/17/14.
 */
public class BaseFragment extends Fragment {

    @Inject
    public NetworkingManager mNetworkingManager;

    @Inject
    public Spinner spinner;

    @Inject
    public ErrorReporter errorReporter;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
    }
}

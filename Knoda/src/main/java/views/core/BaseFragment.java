package views.core;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by nick on 1/17/14.
 */
public class BaseFragment extends Fragment {
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).inject(this);
    }
}

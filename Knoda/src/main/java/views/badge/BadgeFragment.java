package views.badge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knoda.knoda.R;

import butterknife.ButterKnife;
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
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}

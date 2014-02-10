package views.core;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import unsorted.ErrorReporter;
import managers.NetworkingManager;
import managers.UserManager;

/**
 * Created by nick on 1/17/14.
 */
public class BaseFragment extends Fragment {

    @Inject public NetworkingManager networkingManager;

    @Inject public Spinner spinner;

    @Inject public ErrorReporter errorReporter;

    @Inject public UserManager userManager;

    @Override public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
    }


    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void pushFragment(Fragment fragment) {
        ((MainActivity) getActivity()).pushFragment(fragment);
    }

    public void popFragment() {
        ((MainActivity) getActivity()).popFragment();
    }

}

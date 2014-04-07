package views.core;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.UserManager;
import unsorted.ErrorReporter;

/**
 * Created by nick on 1/17/14.
 */
public class BaseFragment extends Fragment {

    @Inject public NetworkingManager networkingManager;

    @Inject public Spinner spinner;

    @Inject public ErrorReporter errorReporter;

    @Inject public UserManager userManager;

    @Inject public Bus bus;

    @Inject public SharedPrefManager sharedPrefManager;

    @Override public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }


    public void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException ex) {

        }
    }

    public void showKeyboard(View focusable) {
        focusable.requestFocus();
        InputMethodManager keyboard = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void pushFragment(Fragment fragment) {
        ((MainActivity) getActivity()).pushFragment(fragment);
    }

    public void popFragment() {
        hideKeyboard();
        ((MainActivity) getActivity()).popFragment();
    }

    public void popToRootFragment() {
        hideKeyboard();
        ((MainActivity) getActivity()).popToRootFragment();
    }

    public void setTitle(String title) {
        ((MainActivity) getActivity()).setActionBarTitle(title);
    }

}

package views.settings;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.knoda.knoda.R;

import butterknife.OnClick;
import managers.UserManager;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import views.core.BaseFragment;
import views.core.MainActivity;

public class SettingsFragment extends BaseFragment {

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @OnClick(R.id.pushNotifications)
    public void onPush() {
        loadPush();
    }

    @OnClick(R.id.profileSettings)
    public void onProfile() {
        loadProfile();
    }

    @OnClick(R.id.about)
    public void onAbout() {
        loadAbout();
    }

    @OnClick(R.id.logout_button)
    public void OnLogout() {
        onClickSignOut();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle b) {
        View view = layoutInflater.inflate(R.layout.fragment_settings, container, false);
        buildPage(view);
        return view;
    }

    public void buildPage(View v) {
        Button logout = (Button) v.findViewById(R.id.logout_button);
        if (((MainActivity) getActivity()).userManager.getUser() != null) {
            logout.setText("Log Out " + ((MainActivity) getActivity()).userManager.getUser().username);
        } else
            logout.setVisibility(View.INVISIBLE);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.global, menu);
        menu.removeItem(R.id.action_search);
        ((MainActivity) getActivity()).setActionBarTitle("SETTINGS");
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void loadPush() {
        SettingsPushFragment fragment = new SettingsPushFragment();
        ((MainActivity) getActivity()).pushFragment(fragment);
    }

    private void loadProfile() {
        SettingsProfileFragment fragment = new SettingsProfileFragment();
        ((MainActivity) getActivity()).pushFragment(fragment);
    }

    private void loadAbout() {
        SettingsAboutFragment fragment = new SettingsAboutFragment();
        ((MainActivity) getActivity()).pushFragment(fragment);
    }

    void onClickSignOut() {
        final UserManager userManager = ((MainActivity) getActivity()).userManager;
        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setTitle("Are you sure you wish to sign out?")
                .create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                userManager.signout(new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User u, ServerError error) {
                        ((MainActivity) getActivity()).spinner.show();
                        alert.dismiss();
                        ((MainActivity) getActivity()).restart();
                    }
                });
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });

    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);
        ((MainActivity) getActivity()).hideNavbar();

        if (((MainActivity) getActivity()).isDebuggable(getActivity().getApplicationContext())) {
            getView().findViewById(R.id.switchAPI).setVisibility(View.VISIBLE);
            String s = "Switch API from Prod to Staging";
            if (networkingManager.baseUrl.contains("captaincold")) {
                s = "Switch API from Staging to Prod";
            }
            ((TextView) getView().findViewById(R.id.switchAPItext)).setText(s);
            getView().findViewById(R.id.switchAPI).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final UserManager userManager = ((MainActivity) getActivity()).userManager;
                    final AlertDialog alert = new AlertDialog.Builder(getActivity())
                            .setPositiveButton("Yes", null)
                            .setNegativeButton("No", null)
                            .setTitle("Are you sure you want to switch the API?")
                            .create();
                    alert.show();
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            userManager.signout(new NetworkCallback<User>() {
                                @Override
                                public void completionHandler(User u, ServerError error) {
                                    String url = "http://captaincold.knoda.com/api/";
                                    if (networkingManager.baseUrl.contains("captaincold")) {
                                        url = "http://api.knoda.com/api/";
                                    }
                                    sharedPrefManager.setAPIurl(url);
                                    ((MainActivity) getActivity()).spinner.show();
                                    alert.dismiss();
                                    ((MainActivity) getActivity()).restart();
                                }
                            });
                        }
                    });
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showNavbar();
    }


}

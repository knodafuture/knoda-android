package views.settings;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;

import com.knoda.knoda.R;

import views.core.MainActivity;

public class SettingsFragment extends PreferenceFragment {

    private PreferenceScreen preferenceScreen;

    Preference.OnPreferenceClickListener changeListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("push")) {
                loadPush();
            } else if (preference.getKey().equals("profile")) {
                loadProfile();
            } else if (preference.getKey().equals("about")) {
                loadAbout();
            }
            return false;
        }
    };


    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.layout.fragment_settings);
        getActivity().invalidateOptionsMenu();
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        preferenceScreen = this.getPreferenceScreen();

        buildPage();

    }

    public void buildPage() {
        Context c = getActivity();
        Preference p1 = new Preference(c);
        p1.setTitle("Push Notification");
        p1.setKey("push");
        p1.setOnPreferenceClickListener(changeListener);
        preferenceScreen.addPreference(p1);

        Preference p2 = new Preference(c);
        p2.setTitle("Profile Settings");
        p2.setKey("profile");
        p2.setOnPreferenceClickListener(changeListener);
        preferenceScreen.addPreference(p2);

        Preference p3 = new Preference(c);
        p3.setTitle("About");
        p3.setKey("about");
        p3.setOnPreferenceClickListener(changeListener);
        preferenceScreen.addPreference(p3);

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
        menu.removeItem(R.id.action_add_prediction);
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


}

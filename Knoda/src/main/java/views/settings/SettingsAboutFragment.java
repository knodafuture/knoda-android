package views.settings;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.knoda.knoda.R;

import managers.NetworkingManager;
import views.core.MainActivity;

public class SettingsAboutFragment extends PreferenceFragment {

    Preference.OnPreferenceClickListener changeListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("terms")) {
                openUrl(NetworkingManager.termsOfServiceUrl);
            } else if (preference.getKey().equals("privacy")) {
                openUrl(NetworkingManager.privacyPolicyUrl);
            } else if (preference.getKey().equals("support")) {
                openUrl(NetworkingManager.supportUrl);
            }
            return false;
        }
    };
    private PreferenceScreen preferenceScreen;

    public static SettingsAboutFragment newInstance() {
        SettingsAboutFragment fragment = new SettingsAboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.layout.fragment_settingspush);
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
        p1.setTitle("Version");
        p1.setKey("version");
        try {
            p1.setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (Exception e) {
        }
        p1.setOnPreferenceClickListener(changeListener);
        preferenceScreen.addPreference(p1);

        Preference p2 = new Preference(c);
        p2.setTitle("View Terms of Service");
        p2.setKey("terms");
        p2.setOnPreferenceClickListener(changeListener);
        preferenceScreen.addPreference(p2);

        Preference p3 = new Preference(c);
        p3.setTitle("View Privacy Policy");
        p3.setKey("privacy");
        p3.setOnPreferenceClickListener(changeListener);
        preferenceScreen.addPreference(p3);

        Preference p4 = new Preference(c);
        p4.setTitle("Contact Support");
        p4.setKey("support");
        p4.setOnPreferenceClickListener(changeListener);
        preferenceScreen.addPreference(p4);

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
        ((MainActivity) getActivity()).setActionBarTitle("ABOUT");
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);
        ((MainActivity) getActivity()).hideNavbar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showNavbar();
    }


}

package views.settings;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.HashMap;

import models.ServerError;
import models.Setting;
import networking.NetworkCallback;
import views.core.MainActivity;

public class SettingsPushFragment extends PreferenceFragment {

    HashMap<String, ArrayList<Setting>> settings;
    Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            int key = Integer.parseInt(preference.getKey());
            Setting s = new Setting();
            s.id = key;
            s.active = (Boolean) newValue;
            s.displayName = preference.getTitle().toString();
            s.description = preference.getSummary().toString();
            ((MainActivity) getActivity()).networkingManager.changeSetting(s, new NetworkCallback<Setting>() {
                @Override
                public void completionHandler(Setting object, ServerError error) {
                    if (error == null) {
                        Toast.makeText(getActivity(), "Setting '" + object.displayName
                                + "' successfully changed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Setting failed to change", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        }
    };
    private PreferenceScreen preferenceScreen;

    public static SettingsPushFragment newInstance() {
        SettingsPushFragment fragment = new SettingsPushFragment();
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
        settings = ((MainActivity) getActivity()).settings;
        preferenceScreen = this.getPreferenceScreen();

        buildPage();

    }

    public void buildPage() {
        Context c = getActivity();
        for (String key : settings.keySet()) {
            //PreferenceCategory category = new PreferenceCategory(c);
            //preferenceScreen.addPreference(category);
            //category.setTitle(key);
            for (Setting s : settings.get(key)) {
                CheckBoxPreference check = new CheckBoxPreference(c);
                check.setOnPreferenceChangeListener(changeListener);
                check.setTitle(s.displayName);
                check.setSummary(s.description);
                check.setChecked(s.active);
                check.setKey(s.id + "");
                preferenceScreen.addPreference(check);
            }

        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.global, menu);
        menu.clear();
        menu.removeItem(R.id.home_actionbar);
        ((MainActivity) getActivity()).setActionBarTitle("PUSH NOTIFICATIONS");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

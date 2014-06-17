package views.settings;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.knoda.knoda.R;

import java.util.ArrayList;

import models.BaseModel;
import models.ServerError;
import models.Setting;
import networking.NetworkCallback;
import unsorted.Logger;
import views.core.MainActivity;

public class SettingsFragment extends PreferenceFragment {

    private PreferenceScreen preferenceScreen;
    ArrayList<Setting> settings;

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
        Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int key = Integer.parseInt(preference.getKey());
                Setting s = new Setting();
                s.id=key;
                s.active=(Boolean)newValue;
                s.displayName=preference.getTitle().toString();
                s.description=preference.getSummary().toString();
                ((MainActivity)getActivity()).networkingManager.changeSetting(s, new NetworkCallback<BaseModel>() {
                    @Override
                    public void completionHandler(BaseModel object, ServerError error) {
                        if(error==null){
                            ArrayList<Setting> settings1=((MainActivity) getActivity()).userManager.getUser().settings;
                            for(Setting s:settings1){
                                if(s.id==((Setting)object).id){
                                    s.active=((Setting)object).active;
                                }
                            }
                            Toast.makeText(getActivity(),"Setting '"+ ((Setting)object).displayName +"' successfully changed",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),"Setting failed to change",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }
        };
        settings = ((MainActivity) getActivity()).userManager.getUser().settings;

        //Create Preferences
        for (Setting s : settings) {
            CheckBoxPreference c = new CheckBoxPreference(getActivity());
            c.setOnPreferenceChangeListener(changeListener);
            c.setTitle(s.displayName);
            c.setSummary(s.description);
            c.setChecked(s.active);
            c.setKey(s.id + "");
            preferenceScreen.addPreference(c);
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
        menu.removeItem(R.id.action_search);
        menu.removeItem(R.id.action_add_prediction);
        ((MainActivity) getActivity()).setActionBarTitle("SETTINGS");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        //if (itemId == R.id.action_settings)

        return super.onOptionsItemSelected(item);

    }


}

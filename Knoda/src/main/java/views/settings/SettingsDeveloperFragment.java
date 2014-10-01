package views.settings;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.knoda.knoda.R;

import views.core.MainActivity;
import views.core.Spinner;

public class SettingsDeveloperFragment extends PreferenceFragment {
    Preference.OnPreferenceClickListener clickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("gcm")) {
                String gcmid = ((MainActivity) getActivity()).gcmManager.getRegistrationId();
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(gcmid);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("gcm", gcmid);
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(getActivity(), "GCM ID copied to clipboard", Toast.LENGTH_SHORT).show();
            } else if (preference.getKey().equals("spinner")) {
                final Spinner spinner = ((MainActivity) getActivity()).spinner;
                spinner.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spinner.hide();
                    }
                }, 5000);
            }
            return false;
        }
    };
    private PreferenceScreen preferenceScreen;

    public static SettingsDeveloperFragment newInstance() {
        SettingsDeveloperFragment fragment = new SettingsDeveloperFragment();
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
    }

    public void buildPage() {
        preferenceScreen.removeAll();

        if (((MainActivity) getActivity()).gcmManager != null && ((MainActivity) getActivity()).gcmManager.getRegistrationId() != null) {
            Context c = getActivity();
            Preference p1 = new Preference(c);
            p1.setTitle("GCM ID");
            p1.setSummary(((MainActivity) getActivity()).gcmManager.getRegistrationId());
            p1.setKey("gcm");
            p1.setOnPreferenceClickListener(clickListener);
            preferenceScreen.addPreference(p1);
        }
        Context c = getActivity();
        Preference p2 = new Preference(c);
        p2.setTitle("Spinner");
        p2.setSummary("Show, then hide spinner");
        p2.setKey("spinner");
        p2.setOnPreferenceClickListener(clickListener);
        preferenceScreen.addPreference(p2);

    }

    @Override
    public void onResume() {
        super.onResume();
        buildPage();


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
        ((MainActivity) getActivity()).setActionBarTitle("DEVELOPER SETTINGS");
        super.onCreateOptionsMenu(menu, inflater);
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

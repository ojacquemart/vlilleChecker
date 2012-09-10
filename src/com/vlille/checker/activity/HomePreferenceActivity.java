package com.vlille.checker.activity;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.maps.LocationManagerWrapper;
import com.vlille.checker.utils.PreferenceKeys;

public class HomePreferenceActivity extends SherlockPreferenceActivity {

	private final String LOG_TAG = getClass().getSimpleName();

	private LocationManagerWrapper locationManagerWrapper;
	private boolean hasClickedOnLocalisationActivationAndNeedsGpsCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		locationManagerWrapper = new LocationManagerWrapper(this);

		final Preference localisationPreference = findPreference(PreferenceKeys.LOCALISATION_GPS_ACTIVATED);
		localisationPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (preference.getKey().equals(PreferenceKeys.LOCALISATION_GPS_ACTIVATED)) {
					boolean enabled = (Boolean) newValue;
					if (enabled && !locationManagerWrapper.isGpsProviderEnabled()) {
						Log.d(LOG_TAG, "Localisation enabled and provider is off");

						hasClickedOnLocalisationActivationAndNeedsGpsCheck = true;
						locationManagerWrapper.createGpsDisabledAlert();

						return false;
					}
				}

				return true;
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (hasClickedOnLocalisationActivationAndNeedsGpsCheck) {
			if (locationManagerWrapper.isGpsProviderEnabled()) {
				hasClickedOnLocalisationActivationAndNeedsGpsCheck = false;

				Log.d(LOG_TAG, "Gps has been activated, set maps location prefs enabled on");
				final Editor editor = findPreference(PreferenceKeys.LOCALISATION_GPS_ACTIVATED).getEditor();
				editor.putBoolean(PreferenceKeys.LOCALISATION_GPS_ACTIVATED, true);
				editor.commit();
				
				// Restart activity to refresh the preferences.
				startActivity(getIntent());
			}

		}
	}

}

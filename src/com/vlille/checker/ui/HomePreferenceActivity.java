package com.vlille.checker.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.ui.osm.location.LocationManagerWrapper;
import com.vlille.checker.utils.PreferenceKeys;

public class HomePreferenceActivity extends SherlockPreferenceActivity {

	private final String TAG = getClass().getSimpleName();

	private LocationManagerWrapper locationManagerWrapper;
	private boolean hasClickedOnLocalisationActivationAndNeedsGpsCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		addPreferencesFromResource(R.xml.preferences);
		
		locationManagerWrapper = LocationManagerWrapper.with(this);
		
		final Preference lastUpdatePreference = findPreference(PreferenceKeys.DATA_STATUS_LAST_UPDATE);
		lastUpdatePreference.setSummary(getDataStatusLastUpdateMessage());

		final Preference localisationPreference = findPreference(PreferenceKeys.LOCALISATION_GPS_ACTIVATED);
		localisationPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (preference.getKey().equals(PreferenceKeys.LOCALISATION_GPS_ACTIVATED)) {
					boolean enabled = (Boolean) newValue;
					if (enabled && !locationManagerWrapper.isGpsProviderEnabled()) {
						Log.d(TAG, "Localisation enabled and provider is off");

						hasClickedOnLocalisationActivationAndNeedsGpsCheck = true;
						locationManagerWrapper.createGpsDisabledAlert();

						return false;
					}
				}

				return true;
			}
		});

	}

	private String getDataStatusLastUpdateMessage() {
		final Metadata metadata = VlilleChecker.getDbAdapter().findMetadata();
		final Date lastUpdate = new Date(metadata.getLastUpdate());
		final String formatPattern = getString(R.string.data_status_date_pattern);
		final String lastUpdateFormatted = new SimpleDateFormat(formatPattern).format(lastUpdate);
		
		return getString(R.string.data_status_last_update_summary, lastUpdateFormatted);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (hasClickedOnLocalisationActivationAndNeedsGpsCheck) {
			if (locationManagerWrapper.isGpsProviderEnabled()) {
				hasClickedOnLocalisationActivationAndNeedsGpsCheck = false;

				Log.d(TAG, "Gps has been activated, set maps location prefs enabled on");
				final Editor editor = findPreference(PreferenceKeys.LOCALISATION_GPS_ACTIVATED).getEditor();
				editor.putBoolean(PreferenceKeys.LOCALISATION_GPS_ACTIVATED, true);
				editor.commit();
				
				// Restart activity to refresh the preferences.
				startActivity(getIntent());
			}

		}
	}

}

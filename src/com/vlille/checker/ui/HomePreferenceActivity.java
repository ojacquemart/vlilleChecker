package com.vlille.checker.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.ui.osm.location.LocationManagerWrapper;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.PreferenceKeys;

public class HomePreferenceActivity extends SherlockPreferenceActivity implements OnSeekBarChangeListener {

	private final String TAG = getClass().getSimpleName();
	
	private LocationManagerWrapper locationManagerWrapper;
	private boolean hasClickedOnLocalisationActivationAndNeedsGpsCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		addPreferencesFromResource(R.xml.preferences);
		
		locationManagerWrapper = LocationManagerWrapper.with(this);
		
		setLastDataStatusUpdate();
		onChangeGpsActivated();
		onChangeRadiusValue();
	}

	private void setLastDataStatusUpdate() {
		final Preference lastUpdatePreference = findPreference(PreferenceKeys.DATA_STATUS_LAST_UPDATE);
		lastUpdatePreference.setSummary(getDataStatusLastUpdateMessage());
	}

	private void onChangeGpsActivated() {
		final Preference prefGpsProviderOn = findPreference(PreferenceKeys.LOCALISATION_GPS_ACTIVATED);
		prefGpsProviderOn.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

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
	
	//=========================
	// Location radius onChange
	//=========================
	
	private Preference prefLocationRadiusValue;
	private SeekBar seekBar;
	private TextView textProgress;
	
	private void onChangeRadiusValue() {
		prefLocationRadiusValue = findPreference(PreferenceKeys.POSITION_RADIUS);
		prefLocationRadiusValue.setOnPreferenceClickListener(
				new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Log.d(TAG, "Show the dialog with slider radius");

						View view = View.inflate(HomePreferenceActivity.this,
								R.layout.position_prefs, null);
						AlertDialog alertDialog = new AlertDialog.Builder(HomePreferenceActivity.this)
								.setTitle(getString(R.string.prefs_position_distance))
								.setView(view)
								.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										int progress = seekBar.getProgress();
										Log.d(TAG, "Save the radius " + progress);
										
										if (progress == 0) {
											progress = (int) PreferenceKeys.POSITION_RADIUS_DEFAULT_VALUE;
										}
										
										final Editor editor = prefLocationRadiusValue.getEditor();
										editor.putLong(PreferenceKeys.POSITION_RADIUS, Long.valueOf(progress));
										editor.commit();
										
										changePrefLocationRadiusValue();
									}
								})
								.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

									}
								})
								.create();
						alertDialog.show();

						textProgress = (TextView) alertDialog.findViewById(R.id.position_distance_value);
						textProgress.setTextColor(getResources().getColor(R.color.black));
						
						seekBar = (SeekBar) alertDialog.findViewById(R.id.position_seekbar_distance);
						seekBar.setOnSeekBarChangeListener(HomePreferenceActivity.this);
						
						updateSeekBarProgress();
						changePrefLocationRadiusValue();

						return false;
					}
		});
		
		changePrefLocationRadiusValue();
	}

	/**
	 * Build the summary displayed in the menu.
	 */
	private void changePrefLocationRadiusValue() {
		String summary = String.format(
					"%s %d%s",
					getString(R.string.prefs_position_radius_distance_summary),
					ContextHelper.getRadiusValue(this),
					getString(R.string.prefs_position_radius_distance_unit));
		
		prefLocationRadiusValue.setSummary(summary);
	}
	
	private void updateSeekBarProgress() {
		seekBar.setProgress(Long.valueOf(ContextHelper.getRadiusValue(this)).intValue());
	}
	
	/**
	 * On radius change, update the text.
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		textProgress.setText(String.format("%d %s", progress, getString(R.string.prefs_position_radius_distance_unit)));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

}

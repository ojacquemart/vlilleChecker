package com.vlille.checker.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.vlille.checker.R;
import com.vlille.checker.utils.PreferenceKeys;

public class LocationMapsPreferenceActivity extends PreferenceActivity implements OnSeekBarChangeListener {

	private final String LOG_TAG = getClass().getSimpleName();

	private Preference preferencePositionDistance;
	private SeekBar seekBar;
	private TextView textProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.position_preferences);

		preferencePositionDistance = findPreference(PreferenceKeys.POSITION_RADIUS);
		preferencePositionDistance.setOnPreferenceClickListener(
				new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Log.d(LOG_TAG, "Show the dialog with slider radius");

						View view = View.inflate(LocationMapsPreferenceActivity.this,
								R.layout.position_prefs, null);
						AlertDialog alertDialog = new AlertDialog.Builder(LocationMapsPreferenceActivity.this)
								.setTitle(getString(R.string.prefs_position_distance))
								.setView(view)
								.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										int progress = seekBar.getProgress();
										Log.d(LOG_TAG, "Save the radius " + progress);
										
										final Editor editor = preferencePositionDistance.getEditor();
										editor.putLong(PreferenceKeys.POSITION_RADIUS, new Long(progress));
										editor.commit();
										
										changePreferencePositionDistanceSummary();
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
						
						seekBar = (SeekBar) alertDialog.findViewById(R.id.position_seekbar_distance);
						seekBar.setOnSeekBarChangeListener(LocationMapsPreferenceActivity.this);
						
						updateSeekBarProgress();
						changePreferencePositionDistanceSummary();

						return false;
					}
		});
		
		changePreferencePositionDistanceSummary();
	}
	
	private void changePreferencePositionDistanceSummary() {
		StringBuilder builder = new StringBuilder();
		builder.append(getString(R.string.prefs_position_radius_distance_summary));
		builder.append(" ");
		builder.append(getRadiusValue());
		builder.append(getString(R.string.prefs_position_radius_distance_unit));
		
		preferencePositionDistance.setSummary(builder.toString());
	}
	
	private void updateSeekBarProgress() {
		final long defaultRadiusValue = getRadiusValue();
		seekBar.setProgress(Long.valueOf(defaultRadiusValue).intValue());
	}

	private long getRadiusValue() {
		return preferencePositionDistance.getSharedPreferences()
				.getLong(PreferenceKeys.POSITION_RADIUS, 0L);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		textProgress.setText("" + String.valueOf(progress) + getString(R.string.prefs_position_radius_distance_unit));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

}

package com.vlille.checker.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockListActivity;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.service.AbstractRetrieverService;
import com.vlille.checker.service.StationsResultReceiver;
import com.vlille.checker.service.StationsResultReceiver.Receiver;
import com.vlille.checker.service.StationsRetrieverService;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.MiscUtils;

/**
 * Home Vlille checker activity.
 */
public class HomeActivity extends SherlockListActivity implements Receiver {

	private final String LOG_TAG = getClass().getSimpleName();

	private StationsResultReceiver resultReceiver;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		initProgressDialog();
	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.loading));
	}

	@Override
	public void onPause() {
		super.onPause();

		if (resultReceiver != null) {
			// Clear receiver so no leaks.
			resultReceiver.setReceiver(null);
		}
	}

	/**
	 * onResume refresh data.
	 * 
	 * @see #onReceiveResult(int, Bundle) for data retrieving.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		final List<Station> starredStations = VlilleChecker.getDbAdapter().getStarredStations();

		boolean isEmptyStarredStations = starredStations.isEmpty();
		Log.d(LOG_TAG, "Starred stations empty? " + isEmptyStarredStations);
		if (isEmptyStarredStations) {
			showBoxNewStation(null);
		} else {
			handleStarredStations(starredStations);
		}
	}

	private void handleStarredStations(List<Station> starredIdsStations) {
		if (ContextHelper.isNetworkAvailable(this)) {
			if (!isFinishing()) {
				progressDialog.show();
			}

			Log.d(LOG_TAG, "Start retriever service.");
			resultReceiver = new StationsResultReceiver(new Handler());
			resultReceiver.setReceiver(this);

			final Intent intent = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(),
					StationsRetrieverService.class);
			intent.putExtra(RECEIVER, resultReceiver);
			intent.putExtra(AbstractRetrieverService.EXTRA_DATA, (ArrayList<Station>) starredIdsStations);
			startService(intent);
		} else {
			Log.d(LOG_TAG, "No network, show the retry view");

			showBoxError(true);
		}
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		boolean finished = false;
		boolean error = false;

		switch (resultCode) {
		case Receiver.RUNNING:
			Log.d(LOG_TAG, "Retrieve in progress");

			break;
		case Receiver.FINISHED:
			Log.d(LOG_TAG, "All starred stations loaded");
			finished = true;

			@SuppressWarnings("unchecked")
			List<Station> results = (List<Station>) resultData.getSerializable(AbstractRetrieverService.RESULTS);

			showBoxNewStation(results);
			handleAdapter(results);

			break;
		case Receiver.ERROR:
			finished = true;
			error = true;

			break;
		}

		if (finished && !isFinishing() && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		showBoxError(error);
	}

	/**
	 * Handle adapter listview
	 * 
	 * @param stations
	 *            The starred stations details.
	 */
	private void handleAdapter(final List<Station> stations) {
		final LinearLayout boxAddStation = (LinearLayout) findViewById(R.id.home_station_new_box);
		final HomeAdapter adapter = new HomeAdapter(this, R.layout.home_list_stations, stations, boxAddStation);
		setListAdapter(adapter);
	}

	/**
	 * Display the add new button if there are no stations in preferences.
	 * 
	 * @param stations
	 *            The starred stations details.
	 */
	private void showBoxNewStation(List<Station> stations) {
		boolean show = stations == null || stations.isEmpty();

		MiscUtils.showOrMask((LinearLayout) findViewById(R.id.home_station_new_box), show);
	}

	/**
	 * Display the add new button if there are no stations in preferences.
	 * 
	 * @param stations
	 *            The starred stations details.
	 */
	private void showBoxError(boolean show) {
		MiscUtils.showOrMask((RelativeLayout) findViewById(R.id.home_error_box), show);
	}

	// UI Menu creation.

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// Location stations from position.
		menu.add(getString(R.string.location)).setIcon(R.drawable.ic_menu_mylocation_ics)
				.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
						startActivity(new Intent(getApplicationContext(), LocationMapsActivity.class));

						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// Add station from maps.
		menu.add(getString(R.string.quickdialog_maps)).setIcon(R.drawable.ic_menu_mapmode_ics)
				.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
						startActivity(new Intent(getApplicationContext(), MapsActivity.class));

						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// Add station from list.
		menu.add(getString(R.string.quickdialog_stations_list)).setIcon(R.drawable.ic_menu_add_ics)
				.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
						startActivity(new Intent(getApplicationContext(), SelectStationsActivity.class));
						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// Refresh stations status.
		menu.add(getString(R.string.refresh)).setIcon(R.drawable.ic_menu_refresh_ics)
				.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
						onResume();

						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		// Contextuel menus
		menu.add(getString(R.string.preferences)).setIcon(R.drawable.ic_menu_preferences_ics)
				.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
						startActivity(new Intent(getApplicationContext(), HomePreferenceActivity.class));

						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		// About
		menu.add(getString(R.string.about_title)).setIcon(R.drawable.ic_menu_info_details_ics)
				.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
						startActivity(new Intent(getApplicationContext(), AboutActivity.class));

						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

}

package com.vlille.checker.ui;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.db.DbAdapter;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.listener.TabListener;
import com.vlille.checker.utils.Constants;
import com.vlille.checker.xml.XMLReader;
import com.vlille.checker.xml.list.StationsListSAXParser;

/**
 * Home Vlille Checker activity.
 */
public class HomeActivity extends SherlockFragmentActivity {

	private final String TAG = getClass().getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		initTabs();
		initSherlockProgressBar();
	}
	
	private void initTabs() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar
				.newTab()
				.setIcon(R.drawable.star)
				.setTabListener(new TabListener<StarsListFragment>(this, "stars", StarsListFragment.class)));
		actionBar.addTab(actionBar
				.newTab()
				.setIcon(R.drawable.view_as_list)
				.setTabListener(new TabListener<AllStationsFragment>(this, "list", AllStationsFragment.class)));
		actionBar.addTab(actionBar
				.newTab()
				.setIcon(R.drawable.map)
				.setTabListener(new TabListener<MapFragment>(this, "map", MapFragment.class)));
	}
	
	private void initSherlockProgressBar() {
		getSherlock().setProgressBarIndeterminate(false);
		getSherlock().setProgressBarIndeterminateVisibility(false);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    Log.d(TAG, "onConfigurationChanged");
	  }

	/**
	 * Create contextual menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// Refresh stations status.
		menu.add(getString(R.string.refresh)).setIcon(R.drawable.refresh_selector)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					/**
					 * @see TabListener for android.R.id.content
					 */
					final Fragment currentFragment = getSupportFragmentManager().findFragmentById(android.R.id.content);
					currentFragment.onResume();
					
					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		// Contextuel menus
		
		// Preferences
		menu.add(getString(R.string.preferences)).setIcon(R.drawable.settings)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					startActivity(new Intent(getApplicationContext(), HomePreferenceActivity.class));
					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		// Update stations data.
		menu.add(getString(R.string.data_launch_update)).setIcon(R.drawable.import_export)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					Log.d(TAG, "Launch data update");
					new AsyncRefreshStationsList().execute();
					
					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		
		// About
		menu.add(getString(R.string.about_title)).setIcon(R.drawable.about)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					startActivity(new Intent(getApplicationContext(), AboutActivity.class));

					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}
	
	/**
	 * {@link AsyncTask} to refresh stations from vlile.fr.
	 */
	class AsyncRefreshStationsList extends AsyncTask<Void, Void, Void> {

		final StationsListSAXParser stationsParser = new StationsListSAXParser();
		private DbAdapter dbAdapter = VlilleChecker.getDbAdapter();
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getSherlock().setProgressBarIndeterminateVisibility(true);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			getSherlock().setProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (insertedNewStations()) {
				toast(R.string.data_status_update_done);
			} else {
				toast(R.string.data_status_uptodate);
			}
			
			Log.d(TAG, "Change last update millis");
			dbAdapter.setLastUpdateTimeToNow();
			
			return null;
		}
		
		/**
		 * Parse vlille stations and compare stations with those from db. 
		 * New stations will be inserted.
		 * 
		 * @return <code>true</code> if new stations have been inserted.
		 */
		private boolean insertedNewStations() {
			Log.d(TAG, "Check if some new stations have been added");
			final List<Station> existingStations = findExistingStations();
			if (existingStations == null) {
				return false;
			}
			
			@SuppressWarnings("unchecked")
			final List<Station> newStations = (List<Station>) CollectionUtils.disjunction(existingStations, dbAdapter.findAll());
			for (Station eachNewStation : newStations) {
				dbAdapter.insertStation(eachNewStation);
			}
			
			return hasNewStations(newStations);
		}

		private List<Station> findExistingStations() {
			final InputStream inputStream = new XMLReader().getInputStream(Constants.URL_STATIONS_LIST);
			if (inputStream == null) {
				return null;
			}
			
			final SetStationsInfos setStationsInfos = stationsParser.parse(inputStream);
			final List<Station> parsedStations = setStationsInfos.getStations();
			
			return parsedStations;
		}
		
		private boolean hasNewStations(List<Station> stationsAdded) {
			final int stationsAddedSize = stationsAdded.size();
			Log.d(TAG, "Nb stations changed: " + stationsAddedSize);
			
			return stationsAddedSize > 0;
		}
		
	}
	
	private void toast(final int resource) {
		HomeActivity.this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), resource, Toast.LENGTH_SHORT).show();
				
			}
		});

	}

}

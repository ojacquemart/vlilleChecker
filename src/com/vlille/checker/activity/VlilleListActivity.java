package com.vlille.checker.activity;

import java.io.InputStream;
import java.util.List;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ApplicationContextHelper;
import com.vlille.checker.utils.Toaster;

/**
 * Base Vlille Activity with helper methods.
 */
public abstract class VlilleListActivity extends ListActivity {
	
	protected final String LOG_TAG_ACTIVITY = this.getClass().getSimpleName();
	
	/**
	 * Checks if network is available.
	 * If no network, display a toast information message.
	 */
	protected boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		final boolean networkIsAvailable = networkInfo != null && networkInfo.isAvailable();
		if (!networkIsAvailable) {
			debug("No network available!");
			Toaster.withContext(getApplicationContext()).noConnection();
		}
		
		return networkIsAvailable;
	}
	
	protected void debug(String text) {
		if (text == null) {
			text = "";
		}
		
		Log.d(LOG_TAG_ACTIVITY, text);
	}
	
	protected InputStream getInputStream() {
		return ApplicationContextHelper.getInputStream(getApplicationContext());
	}
	
	public List<Station> getAllStations() {
		return ApplicationContextHelper.getAllStations(getApplicationContext());
	}
	
	protected SharedPreferences getPrefs() {
		return ApplicationContextHelper.getPrefs(getApplicationContext());
	}
	
	protected boolean isStarred(String stationId) {
		return ApplicationContextHelper.isStarred(getApplicationContext(), stationId);
	}

	protected List<String> getStarredIdsStations() {
		return ApplicationContextHelper.getStarred(getApplicationContext());
	}
	
}

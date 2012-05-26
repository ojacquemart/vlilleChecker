package com.vlille.checker.activity;

import java.io.InputStream;

import android.app.ListActivity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.vlille.checker.R;
import com.vlille.checker.utils.ContextHelper;

/**
 * Base Vlille Activity with helper methods.
 */
public abstract class VlilleListActivity extends ListActivity {
	
	protected final String LOG_TAG = getClass().getSimpleName();
	
	/**
	 * Checks if network is available.
	 * If no network, display a toast information message.
	 */
	protected boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		final boolean networkIsAvailable = networkInfo != null && networkInfo.isAvailable();
		if (!networkIsAvailable) {
			Log.d(LOG_TAG, "No network available!");
			Toast.makeText(this, R.string.error_no_connection, Toast.LENGTH_LONG).show();
		}
		
		return networkIsAvailable;
	}
	
	
	protected InputStream getInputStream() {
		return ContextHelper.getInputStream(getApplicationContext());
	}
	
}

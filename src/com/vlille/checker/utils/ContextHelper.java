package com.vlille.checker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.vlille.checker.ui.StarsListAdapter;

/**
 * Helper for {@link Context}.
 */
public final class ContextHelper {
	
	private ContextHelper() {}
	
	protected final String LOG_TAG = getClass().getSimpleName();
	
	/**
	 * Checks if network is available.
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		return networkInfo != null && networkInfo.isAvailable();
	}
	
	/**
	 * Display or hide the address box for the {@link StarsListAdapter}
	 */
	public static boolean isDisplayingStationAdress(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(PreferenceKeys.HOME_DISPLAY_ADRESS, 
								PreferenceKeys.HOME_DISPLAY_DEFAULT_VALUE);
	}
	
	/**
	 * Gets the radius value.
	 */
	public static long getRadiusValue(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getLong(PreferenceKeys.POSITION_RADIUS,
							PreferenceKeys.POSITION_RADIUS_DEFAULT_VALUE);
	}
}

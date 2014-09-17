package com.vlille.checker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.vlille.checker.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.vlille.checker.utils.PreferenceKeys.*;

/**
 * Helper for {@link Context}.
 */
public final class ContextHelper {
	
	private ContextHelper() {}

	/**
	 * Checks if network is available.
	 */
	public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		final boolean networkAvailable = networkInfo != null && networkInfo.isAvailable();
		if (!networkAvailable) {
			ToastUtils.show(context, R.string.error_no_connection);
		}
		
		return networkAvailable;
	}
	
	public static boolean isStationAddressVisible(Context context) {
		return getBooleanValue(context, STATION_ADDRESS_VISIBLE, STATION_ADDRESS_VISIBLE_DEFAULT_VALUE);
	}

    public static boolean isStationLastUpdateMomentVisible(Context context) {
        return getBooleanValue(context,
                STATION_LAST_UPDATE_VISIBLE, STATION_LAST_UPDATE_VISIBLE_DEFAULT_VALUE);
    }

    /**
     * Gets a preference boolean value
     */
    private static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        return getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

	/**
	 * Gets the radius value.
	 */
	public static long getRadiusValue(Context context) {
		return getDefaultSharedPreferences(context).getLong(POSITION_RADIUS, POSITION_RADIUS_DEFAULT_VALUE);
	}
}

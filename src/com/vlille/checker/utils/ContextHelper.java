package com.vlille.checker.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vlille.checker.activity.HomeAdapter;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.xml.StationsListSAXParser;

/**
 * Helper for {@link Context}.
 */
public class ContextHelper {
	
	/**
	 * Parse all stations general informations.
	 */
	public static SetStationsInfos parseAllStations(Context context) {
		final InputStream inputStream = getInputStream(context);
		StationsListSAXParser stationsListSAXParser = new StationsListSAXParser(inputStream);
		
		return stationsListSAXParser.parse();
	}
	
	
	/**
	 * Input stream from all stations.
	 * @return
	 */
	public static InputStream getInputStream(Context context) {
		try {
			return context.getAssets().open(Constants.FILE_ASSET_STATIONS_LIST);
		} catch (IOException e) {
			Log.e("Error while retrieving xml input stream", e.getMessage());
			throw new IllegalStateException(e);
		}
	}
	
	
	/**
	 * Display or hide the address box for the {@link HomeAdapter}
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

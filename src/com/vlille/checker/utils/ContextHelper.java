package com.vlille.checker.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vlille.checker.activity.HomeAdapter;
import com.vlille.checker.activity.PreferenceKeys;
import com.vlille.checker.activity.SelectStationsActivity;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.Station;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.stations.Constants;
import com.vlille.checker.stations.xml.StationsListSAXParser;

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
	 * Get stations maps informations.
	 */
	public Metadata getMapsInformation(Context context) {
		return parseAllStations(context).getMetadata();
	}
	
	/**
	 * Get all parsed stations.
	 */
	public static List<Station> getAllStations(Context context) {
		return parseAllStations(context).getStations();
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
	 * Global vlille preferences.
	 */
	public static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(SelectStationsActivity.PREFS_FILE, Context.MODE_PRIVATE);
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

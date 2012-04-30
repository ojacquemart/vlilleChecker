package com.vlille.checker.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vlille.checker.activity.HomeAdapter;
import com.vlille.checker.activity.PreferenceKeys;
import com.vlille.checker.activity.SelectStationsActivity;
import com.vlille.checker.model.Station;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.StationsMapsInfos;
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
	public StationsMapsInfos getMapsInformation(Context context) {
		return parseAllStations(context).getMapsInformations();
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
	
	public static boolean isStarred(Context context, String stationId) {
		return getPrefs(context).contains(stationId);
	}
	
	/**
	 * Global vlille preferences.
	 */
	public static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(SelectStationsActivity.PREFS_FILE, Context.MODE_PRIVATE);
	}
	
	/**
	 * Register station id into prefs.
	 * @param context
	 * @param stationId station id
	 * @param selected remove or add the station.
	 */
	public static void registerPrefsStation(Context context, String stationId, boolean selected) {
		final Editor editor = getPrefs(context).edit();
		if (selected) {
			editor.putBoolean(stationId, Boolean.TRUE);
		} else {
			editor.remove(stationId);
		}
		
		editor.commit();
	}
	
	/**
	 * Remove key from sharred preferences.
	 * @param key
	 */
	public static void doPrefsRemove(Context context, String key) {
		getPrefs(context).edit().remove(key).commit();
	}

	/**
	 * Starred stations.
	 */
	public static List<String> getStarred(Context context) {
		List<String> starredStations = new ArrayList<String>();

		SharedPreferences sharedPrefs = getPrefs(context);
		Map<String, ?> allPrefs = sharedPrefs.getAll();
		for (Entry<String, ?> entry : allPrefs.entrySet()) {
			String id = entry.getKey();
			Object value = entry.getValue();
			if (value.getClass().equals(Boolean.class)) {
				starredStations.add(id);
			}
		}

		return starredStations;
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

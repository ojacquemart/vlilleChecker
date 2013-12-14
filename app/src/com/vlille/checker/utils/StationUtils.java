package com.vlille.checker.utils;

import android.location.Location;
import android.util.Log;

import com.vlille.checker.model.Station;

import java.util.ArrayList;
import java.util.List;

public final class StationUtils {
	
	private static final String TAG = "StationUtils";
	
	private StationUtils() {}

	public static List<Station> filter(List<Station> stations, String keyword) {
		if (keyword == null || keyword.length() == 0) {
			return stations;
		}

        keyword = keyword.toLowerCase();

		List<Station> result = new ArrayList<Station>();

		for (Station eachStation : stations) {
			if (eachStation.getName().toLowerCase().contains(keyword)) {
				result.add(eachStation);
			}
		}

		return result;
	}
	
	
	public static boolean isNearCurrentLocation(Station station, Location currentLocation,
			long distanceAroundLocation) {
		// Check distance between current location and station.
		float[] results = new float[3];
		Location.distanceBetween(
				currentLocation.getLatitude(), currentLocation.getLongitude(),
				station.getLatitude(), station.getLongitude(), results);

		float distanceBetweenLocationAndStation = results[0];
		final boolean nearStation = distanceBetweenLocationAndStation <= distanceAroundLocation;

		if (nearStation) {
			Log.d(TAG, "Distance between current location and " + station.getName() + 
					" is [" + distanceBetweenLocationAndStation + "]");
		}

		return nearStation;
	}

}

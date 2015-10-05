package com.vlille.checker.utils;

import android.location.Location;
import android.util.Log;
import com.vlille.checker.model.Station;

public final class StationUtils {
	
	private static final String TAG = StationUtils.class.getSimpleName();
	
	private StationUtils() {}

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

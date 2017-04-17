package com.vlille.checker.ui.osm.location;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

import com.vlille.checker.R;

/**
 * Wrapper for LocationManager.
 */
public class LocationManagerWrapper {
	
	public static int DURATION_UPDATE_IN_MILLIS = 10000;
	public static int DISTANCE_UPDATE_IN_METERS = 10000;
	
	private Context context;
	private LocationManager locationManager;

	private LocationManagerWrapper(Context context) {
		this.context = context;
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static LocationManagerWrapper with(Context context) {
		return new LocationManagerWrapper(context);
	}
	
	public boolean hasCurrentLocation() {
		return getCurrentLocation() != null;
	}
	
	public Location getCurrentLocation() {
		return getLastBestLocation(DISTANCE_UPDATE_IN_METERS, DURATION_UPDATE_IN_MILLIS);
	}

	/**
	   * Returns the most accurate and timely previously detected location.
	   * Where the last result is beyond the specified maximum distance or 
	   * latency a one-off location update is returned via the {@link LocationListener}
	   * specified in {@link setChangedLocationListener}.
	   * @param minDistance Minimum distance before we require a location update.
	   * @param minTime Minimum time required between location updates.
	   * @return The most accurate and / or timely previously detected location.
	   */
	public Location getLastBestLocation(int minDistance, long minTime) {
		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;

		// Iterate through all the providers on the system, keeping
		// note of the most accurate result within the acceptable time limit.
		// If no result is found within maxTime, return the newest Location.
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider : matchingProviders) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();

				if ((time > minTime && accuracy < bestAccuracy)) {
					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;
				} else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime) {
					bestResult = location;
					bestTime = time;
				}
			}
		}

		return bestResult;
	}

	public void checkAndEnableGpsProvider() {
		if (!isGpsProviderEnabled()) {
			createGpsDisabledAlert();
		}
	}
	
	public LocationProvider getBestAvailableProvider() {
		if (isGpsProviderEnabled()) {
			return getGpsProvider();
		}
		
		return locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
	}
	
	public String getBestAvailableProviderName() {
		final LocationProvider bestAvailableProvider = getBestAvailableProvider();
		
		if (bestAvailableProvider != null) {
			return bestAvailableProvider.getName();
		}
		
		return "";
	}

	public boolean isGpsProviderEnabled() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public LocationProvider getGpsProvider() {
		return locationManager.getProvider(LocationManager.GPS_PROVIDER);
	}

	public void createGpsDisabledAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getString(R.string.prefs_location_enable_gps_title)).setCancelable(false)
				.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						showGpsOptions();
					}
				});
		builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivity(gpsOptionsIntent);
	}


	public LocationManager getLocationManager() {
		return locationManager;
	}

}

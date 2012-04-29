package com.vlille.checker.maps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;

import com.vlille.checker.R;

/**
 * Wrapper for LocationManager.
 */
public class LocationManagerWrapper {

	private Context context;
	private LocationManager locationManager;

	public LocationManagerWrapper(Context context) {
		this.context = context;
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public Location getCurrentLocation() {
		Location location = isGpsProviderEnabled() 
				? locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) 
				: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		return location;
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
		builder.setMessage(context.getString(R.string.localisation_activate_gps)).setCancelable(false)
				.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						showGpsOptions();
					}
				});
		builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
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

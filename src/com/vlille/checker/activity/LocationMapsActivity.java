package com.vlille.checker.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.vlille.checker.R;
import com.vlille.checker.maps.LocationManagerWrapper;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ApplicationContextHelper;

public class LocationMapsActivity extends MapsActivity {

	private static final int UPDATE_MIN_DISTANCE = 10;
	private static final int UPDATE_MIN_TIME = 10;
	
	private boolean locationAvailable;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, true);
		
		final String bestAvailableProviderName = new LocationManagerWrapper(this).getBestAvailableProviderName();
		Log.i(LOG_TAG, "Best available provider name " + bestAvailableProviderName);
		
		final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(bestAvailableProviderName, UPDATE_MIN_TIME, UPDATE_MIN_DISTANCE, locationListener);
	}
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d(LOG_TAG, "onStatusChanged");
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			Log.d(LOG_TAG, "onProviderEnabled");
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Log.d(LOG_TAG, "onProviderDisabled");
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Log.d(LOG_TAG, "onLocationChanged");
			onResume();
		}
	};

	@Override
	public void onResume() {
		Log.d(LOG_TAG, "Resume localisation maps");
		
		mapView.updateCurrentLocation();
		locationAvailable = mapView.getCurrentLocation() != null;
		if (!locationAvailable) {
			Log.i(LOG_TAG, "No location found");
			Toast
				.makeText(getApplicationContext(), R.string.error_no_location_found, Toast.LENGTH_LONG)
				.show();
		}
			
		mapView.resetStationsOverlays();
		mapView.centerControllerAndDrawCircleOverlay();
		
		super.onResume();
	}
	
	@Override
	public List<Station> getStations() {
		Log.i(LOG_TAG, "Location available ? " + locationAvailable);
		if (!locationAvailable) {
			return new ArrayList<Station>();
		}
		
		final List<Station> stationsToDraw = new ArrayList<Station>();
		
		final List<Station> stations = setStationsInfos.getStations();
		final Location currentLocation = mapView.getCurrentLocation();
		if (stations != null && currentLocation != null) {
			long parameterDistanceBetweenStations = ApplicationContextHelper.getRadiusValue(this);
			Log.d(LOG_TAG, "Distance between stations parameter = " + parameterDistanceBetweenStations);
			
			for (Station eachStation : stations) {
				boolean drawStation = isDistanceBetweenLocationAndStationOk(currentLocation, eachStation, parameterDistanceBetweenStations);
				if (drawStation) {
					stationsToDraw.add(eachStation);
				}
			}
		}
		
		Log.d(LOG_TAG, "Nb stations to draw = " + stationsToDraw.size());
		if (stationsToDraw.isEmpty()) {
			Toast
				.makeText(getApplicationContext(), R.string.error_no_stations_near_current_location, Toast.LENGTH_LONG)
				.show();
		}
		
		return stationsToDraw;
	}
	
	private boolean isDistanceBetweenLocationAndStationOk(Location mCurrentLocation, Station eachStation, long mParameterDistanceBetweenStations) {
		// Check distance between current location and station.
		float[] results = new float[3];
		Location.distanceBetween(
				mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
				eachStation.getLatitude(), eachStation.getLongitude(), results);
		
		float distanceBetweenLocationAndStation = results[0];
		final boolean nearStation = distanceBetweenLocationAndStation <= mParameterDistanceBetweenStations;
		
		if (nearStation) {
			Log.d(LOG_TAG, "Distance between location and station " + eachStation.getName() + "  " + distanceBetweenLocationAndStation);
		}
		
		return nearStation;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.prefs_location_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, LocationMapsPreferenceActivity.class));

		return true;
	}
	
	@Override
	public void doInitActionBar() {
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.addAction(new RefreshAction());
	}
	
	private class RefreshAction extends AbstractAction {

        public RefreshAction() {
            super(R.drawable.ic_menu_refresh);
        }

        @Override
        public void performAction(View view) {
        	Log.d(LOG_TAG, "Perform update overlays");
        	onResume();
        }

    }

}

package com.vlille.checker.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import com.vlille.checker.maps.PositionConstants;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ContextHelper;

public class LocationMapsActivity extends MapsActivity implements GetStations {

	private boolean locationAvailable;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, true);
		
		final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final List<String> providers = locationManager.getProviders(false);
		for (String eachProviderName : providers) {
			Log.d(LOG_TAG, "Provider enabled " + eachProviderName);
			locationManager.requestLocationUpdates(eachProviderName,
					PositionConstants.DISTANCE_UPDATE_IN_METERS,
					PositionConstants.DURATION_UPDATE_IN_MILLIS,
					locationListener);
		}
	}
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (LocationProvider.AVAILABLE == status) {
				Log.d(LOG_TAG, "onStatusChanged " + provider);
				onResume();
			}
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			Log.d(LOG_TAG, "onProviderEnabled " + provider);
			onResume();
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Log.d(LOG_TAG, "onProviderDisabled " + provider);
		}
		
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				Log.d(LOG_TAG, "onLocationChanged from " + location.getProvider());
				onResume();
			}
		}
	};
	
	@Override
	public void doResume() {
		Log.d(LOG_TAG, "Resume location maps");
		try {
			mapView.updateCurrentLocation();
			
			locationAvailable = mapView.getCurrentLocation() != null;
			if (!locationAvailable) {
				
				Toast.makeText(getApplicationContext(), R.string.error_no_location_found, Toast.LENGTH_SHORT).show();
				throw new IllegalStateException("No location found");
			}
				
			super.doResume();
		} catch (NullPointerException e) {
			throw e;
		}
	}
	
	@Override
	public List<Station> getOnCreateStations() {
		Log.i(LOG_TAG, "Location available ? " + locationAvailable);
		if (!locationAvailable) {
			return new ArrayList<Station>();
		}
		
		final List<Station> stationsToDraw = new ArrayList<Station>();
		
		final Location currentLocation = mapView.getCurrentLocation();
		if (stations != null && currentLocation != null) {
			long parameterDistanceBetweenStations = ContextHelper.getRadiusValue(this);
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
			LocationMapsActivity.this.runOnUiThread(new Runnable() {
				@Override
			    public void run() {
					Toast.makeText(
							getApplicationContext(),
							R.string.error_no_stations_near_current_location,
							Toast.LENGTH_SHORT).show();
			    }
		    });
		}
		
		return stationsToDraw;
	}
	
	@Override
	public List<Station> getOnResumeStations() {
		return getOnCreateStations();
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
			Log.d(LOG_TAG, "Distance between location and station " + eachStation.getName() + " " + distanceBetweenLocationAndStation);
		}
		
		return nearStation;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.position_prefs_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, LocationMapsPreferenceActivity.class));

		return true;
	}
	
	@Override
	public void doInitActionBar() {
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.addAction(new RefreshAction());
	}
	
	private class RefreshAction extends AbstractAction {

        public RefreshAction() {
            super(R.drawable.ic_menu_refresh_ics);
        }

        @Override
        public void performAction(View view) {
        	Log.d(LOG_TAG, "Perform update overlays");
        	onResume();
        }

    }

}

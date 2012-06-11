package com.vlille.checker.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.db.DbAdapter;
import com.vlille.checker.maps.OnPanAndZoomListener;
import com.vlille.checker.maps.VlilleMapView;
import com.vlille.checker.maps.overlay.BallonStationOverlays.StationDetails;
import com.vlille.checker.model.Station;
import com.vlille.checker.service.AbstractRetrieverService;
import com.vlille.checker.service.StationsResultReceiver;
import com.vlille.checker.service.StationsResultReceiver.Receiver;
import com.vlille.checker.service.StationsRetrieverService;
 
/**
 * Select stations from maps.
 * It allows to select your station browsing the stations map.
 */
public class MapsActivity extends MapActivity implements InitializeActionBar, GetStations, Receiver {

	protected final String LOG_TAG = getClass().getSimpleName();
	protected VlilleMapView mapView;
	
	/**
	 * The actionBar with icons and progressive loading.
	 */
	protected ActionBar actionBar;
	
	/**
	 * The stations list stored in db.
	 */
	protected List<Station> stations;
	
	/**
	 * The receiver from the service.
	 */
	private StationsResultReceiver resultReceiver;
	
	/**
	 * Time to wait to initialize the maps. Small hack.
	 * @see #runnableForWaitingMap
	 */
	private static final int TIME_TO_WAIT_IN_MS = 100;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		onCreate(savedInstanceState, false);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (resultReceiver != null) {
			// Clear receiver so no leaks.
			resultReceiver.setReceiver(null);
		}
	}
	
	public void onCreate(Bundle savedInstanceState, boolean locationEnabled) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.maps);
		
		doInitActionBar();
		
		mapView = (VlilleMapView) findViewById(R.id.mapview);
		mapView.setLocationActivated(locationEnabled);
		mapView.setOnPanListener(getOnPanListener());
		
		try {
			final DbAdapter dbAdapter = VlilleChecker.getDbAdapter();
			
			mapView.setMapsInformations(dbAdapter.findMetadata());
			stations = dbAdapter.findAll();
		} catch (RuntimeException e) {
			Log.e(LOG_TAG, "#onCreate() exception", e);
			Toast.makeText(this, getString(R.string.error_no_connection), Toast.LENGTH_LONG);
		}
	}

	public OnPanAndZoomListener getOnPanListener() {
		return new OnPanAndZoomListener() {
			
			@Override
			public void onZoom() {
				Log.d(LOG_TAG, "#onZoom");
				startRetrieverService();
			}
			
			@Override
			public void onPan() {
				Log.d(LOG_TAG, "#onPan");
				startRetrieverService();
			}
		};
	}

	@Override
	public void onResume() {
		Log.d(LOG_TAG, "#onResume");
		try {
			super.onResume();
		
			mapView.postDelayed(runnableForWaitingMap, TIME_TO_WAIT_IN_MS);
			doResume();
		} catch (Exception e) {
			Log.e(LOG_TAG, "#onResume() exception", e);
			Toast.makeText(this, getString(R.string.error_initialization_stations), Toast.LENGTH_SHORT);
		}		
	}
	
	public void doResume() {
		actionBar.setProgressBarVisibility(View.VISIBLE);
		mapView.initCenter();
		startRetrieverService();
	}

   /**
	 * Hack for getting correct map bounds, without it the latitude and longitude are not reliable.
	 * 
	 * @see http://stackoverflow.com/questions/2667386/mapview-getlatitudespan-and-getlongitudespan-not-working
	 * @see http://dev.kafol.net/2011/11/android-google-maps-mapview-hacks.html  
	 */
	private Runnable runnableForWaitingMap = new Runnable() {
		public void run() {
			if (mapView.getLatitudeSpan() == 0 || mapView.getLongitudeSpan() == 360000000) {
				mapView.postDelayed(this, TIME_TO_WAIT_IN_MS);
			} else {
				try {
					new AsyncCreateOverlays().execute();
				
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), R.string.error_initialization_stations, Toast.LENGTH_SHORT).show();
				}
			}
		}

	};
	
	/**
	 * Async create overlays, then start servies to get the bounded details stations.
	 */
	private class AsyncCreateOverlays extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mapView.createOverlays(getOnCreateStations());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			startRetrieverService();
		}
	}
	
	private void startRetrieverService() {
		try {
			actionBar.setProgressBarVisibility(View.VISIBLE);
			resultReceiver = new StationsResultReceiver(new Handler());
			resultReceiver.setReceiver(this);
			
			final Intent intent = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), StationsRetrieverService.class);
			intent.putExtra(RECEIVER, resultReceiver);
			
			intent.putExtra(StationsRetrieverService.EXTRA_DATA, (ArrayList<Station>) getOnResumeStations());
			startService(intent);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error during overlays service", e);
			
		}
	}
	
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		boolean finished = false;
		
		switch (resultCode) {
		case Receiver.RUNNING:
			Log.d(LOG_TAG, "Retrieve in progress");

			break;
		case Receiver.FINISHED:
			Log.d(LOG_TAG, "Bounded overlays stations loaded");
			finished = true;
			
			@SuppressWarnings("unchecked")
			List<Station> results = (List<Station>) resultData.getSerializable(AbstractRetrieverService.RESULTS);
			
			// Copy details infos to overlay to display number bikes and attachs.
			for (Station eachStation : results) {
				final StationDetails overlay = mapView.getOverlayByStationId(eachStation);
				if (overlay != null) {
					overlay.copyDetailledStation(eachStation);
				}
			}
			
			break;
		case Receiver.ERROR:
			finished = true;

			break;
		}
		
		if (finished) {
			mapView.postInvalidate();
			Log.d(LOG_TAG, "hide action bar progress");
			actionBar.setProgressBarVisibility(View.GONE);
		}
	}
	
	public List<Station> getOnCreateStations() {
		return stations;
	}
	
	public List<Station> getOnResumeStations() {
		return mapView.getBoundedStations();
	}	
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
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
        	startRetrieverService();
        }

    }

	
}

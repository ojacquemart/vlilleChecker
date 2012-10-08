package com.vlille.checker.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.vlille.checker.R;
import com.vlille.checker.maps.VlilleMapView;
import com.vlille.checker.model.Station;
import com.vlille.checker.service.StationsResultReceiver;
import com.vlille.checker.utils.ToastUtils;
 
/**
 * Select stations from maps.
 * It allows to select your station browsing the stations map.
 */
public class MapFragment extends SherlockFragment /*implements StationInitializer, Receiver*/ {

	protected final String LOG_TAG = getClass().getSimpleName();
	
	private Activity activity;
	
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
	
	private VlilleMapView map;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreateView");
		
		activity = getActivity();
		final View view = inflater.inflate(R.layout.maps, container, false);
		
		
		map = (VlilleMapView) view.findViewById(R.id.mapview);
		map.setActivity(activity);
		map.invalidate();
		
		addLocationEnablerClickListener(view);
		
		return view;
//		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	}

	private void addLocationEnablerClickListener(final View view) {
		final ImageButton locationEnabler = (ImageButton) view.findViewById(R.id.maps_location_enable);
		locationEnabler.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ToastUtils.show(activity, "Click on location button!");
			}
		});
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
//		// Refresh overlays.
//		menu.add(getString(R.string.refresh)).setIcon(R.drawable.ic_menu_refresh_ics)
//				.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {
//
//					@Override
//					public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
//						onRestart();
//
//						return false;
//					}
//				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//		
//		return true;
//	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (resultReceiver != null) {
			// Clear receiver so no leaks.
			resultReceiver.setReceiver(null);
		}
	}
	
	
	
//	public void onCreate(Bundle savedInstanceState, boolean locationEnabled) {
//		super.onCreate(savedInstanceState);
//		
//		setContentView(R.layout.maps);
//		
//		mapView = (VlilleMapView) findViewById(R.id.mapview);
//		mapView.setLocationActivated(locationEnabled);
//		mapView.setOnPanListener(getOnPanListener());
//		
//		try {
//			final DbAdapter dbAdapter = VlilleChecker.getDbAdapter();
//			
//			mapView.setMapsInformations(dbAdapter.findMetadata());
//			stations = dbAdapter.findAll();
//		} catch (RuntimeException e) {
//			Log.e(LOG_TAG, "#onCreate() exception", e);
//			Toast.makeText(this, getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	public OnPanAndZoomListener getOnPanListener() {
//		return new OnPanAndZoomListener() {
//			
//			@Override
//			public void onZoom() {
//				Log.d(LOG_TAG, "#onZoom");
//				startRetrieverService();
//			}
//			
//			@Override
//			public void onPan() {
//				Log.d(LOG_TAG, "#onPan");
//				startRetrieverService();
//			}
//		};
//	}
//
//	@Override
//	public void onResume() {
//		Log.d(LOG_TAG, "#onResume");
//		try {
//			super.onResume();
//		
//			mapView.postDelayed(runnableForWaitingMap, TIME_TO_WAIT_IN_MS);
//			doResume();
//		} catch (Exception e) {
//			Log.e(LOG_TAG, "#onResume() exception", e);
//			Toast.makeText(this, getString(R.string.error_initialization_stations), Toast.LENGTH_SHORT).show();
//		}		
//	}
//	
//	public void doResume() {
////		setSupportProgressBarIndeterminateVisibility(true);
//		mapView.initCenter();
//		startRetrieverService();
//	}
//
//   /**
//	 * Hack for getting correct map bounds, without it the latitude and longitude are not reliable.
//	 * 
//	 * @see http://stackoverflow.com/questions/2667386/mapview-getlatitudespan-and-getlongitudespan-not-working
//	 * @see http://dev.kafol.net/2011/11/android-google-maps-mapview-hacks.html  
//	 */
//	private Runnable runnableForWaitingMap = new Runnable() {
//		@Override
//		public void run() {
//			if (mapView.getLatitudeSpan() == 0 || mapView.getLongitudeSpan() == 360000000) {
//				mapView.postDelayed(this, TIME_TO_WAIT_IN_MS);
//			} else {
////				try {
////					new AsyncCreateOverlays().execute();
////				
////				} catch (Exception e) {
////					Toast.makeText(this, R.string.error_initialization_stations, Toast.LENGTH_SHORT).show();
////				}
//			}
//		}
//
//	};
//	
//	/**
//	 * Async create overlays, then start servies to get the bounded details stations.
//	 */
//	private class AsyncCreateOverlays extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			if (isNetworkAvailable()) {
//				mapView.createOverlays(getOnCreateStations());
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			startRetrieverService();
//		}
//	}
//	
//	private boolean isNetworkAvailable() {
//		final boolean networkAvailable = ContextHelper.isNetworkAvailable(this);
//		if (!networkAvailable) {
////			MapFragment.this.runOnUiThread(new Runnable() {
////				
////				@Override
////				public void run() {
////					Toast.makeText(getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_SHORT).show();
////				}
////			});
//		}
//		
//		return networkAvailable;
//	}
//	
//	private void startRetrieverService() {
//		try { 
////			setSupportProgressBarIndeterminateVisibility(true);
//			resultReceiver = new StationsResultReceiver(new Handler());
//			resultReceiver.setReceiver(this);
//			
//			final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, StationsRetrieverService.class);
//			intent.putExtra(RECEIVER, resultReceiver);
//			
//			intent.putExtra(AbstractRetrieverService.EXTRA_DATA, (ArrayList<Station>) getOnResumeStations());
//			startService(intent);
//		} catch (Exception e) {
//			Log.e(LOG_TAG, "Error during overlays service", e);
////			setSupportProgressBarIndeterminateVisibility(false);
//		}
//	}
//	
//	@Override
//	public void onReceiveResult(int resultCode, Bundle resultData) {
//		boolean finished = false;
//		
//		switch (resultCode) {
//		case Receiver.RUNNING:
//			Log.d(LOG_TAG, "Retrieve in progress");
//
//			break;
//		case Receiver.FINISHED:
//			Log.d(LOG_TAG, "Bounded overlays stations loaded");
//			finished = true;
//			
//			@SuppressWarnings("unchecked")
//			final List<Station> results = (List<Station>) resultData.getSerializable(AbstractRetrieverService.RESULTS);
//			
////			MapFragment.this.runOnUiThread(new Runnable() {
////				@Override
////				public void run() {
////					// Copy details infos to overlay to display number bikes and attachs.
////					for (Station eachStation : results) {
////						final StationDetails overlay = mapView.getOverlayByStationId(eachStation);
////						if (overlay != null) {
////							overlay.copyDetailledStation(eachStation);
////						}
////					}
////					
////				}
////			});
//			
//			break;
//		case Receiver.ERROR:
//			finished = true;
//
//			break;
//		}
//		
//		if (finished) {
//			mapView.postInvalidate();
//			Log.d(LOG_TAG, "#onReceiveResult finished");
////			setSupportProgressBarIndeterminateVisibility(false);
//		}
//	}
//	
//	@Override
//	public List<Station> getOnCreateStations() {
//		return stations;
//	}
//	
//	@Override
//	public List<Station> getOnResumeStations() {
//		return mapView.getBoundedStations();
//	}
//
//	@Override
//	protected boolean isRouteDisplayed() {
//		return false;
//	}	

}

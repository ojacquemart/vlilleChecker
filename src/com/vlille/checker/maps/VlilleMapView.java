package com.vlille.checker.maps;

import java.util.List;

import org.apache.commons.lang3.time.StopWatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.vlille.checker.R;
import com.vlille.checker.maps.overlay.PositionCircleOverlay;
import com.vlille.checker.maps.overlay.StationsOverlays;
import com.vlille.checker.maps.overlay.StationsOverlays.MyOverlayItem;
import com.vlille.checker.model.Station;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.stations.xml.Loader;

/**
 * @see http://stackoverflow.com/questions/4729255/how-to-implemennt-onzoomlistener-on-mapview
 */
public class VlilleMapView extends MapView {

	private static final int DETAILLED_ZOOM_LEVEL = 17;
	private static final int TIME_TO_WAIT_IN_MS = 100;
	
	private final String LOG_TAG = getClass().getSimpleName();
	private final Drawable DEFAULT_MARKER = getResources().getDrawable(R.drawable.station_marker);
	
	private LocationManagerWrapper locationManagerWrapper = new LocationManagerWrapper(getContext());
	private boolean mLocationEnabled;
    private int mOldZoomLevel = -1;
    private Location mCurrentLocation = null;
    
    private GeoPoint mOldCenterGeoPoint;
    private OnPanAndZoomListener mListener;
    
    private Metadata metadata;
    private List<Station> stations;
    private StationsOverlays mStationsOverlays;

    public VlilleMapView(Context context, String apiKey) {
        super(context, apiKey);
        init();
    }

    public VlilleMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VlilleMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    public void setMapsInformations(Metadata mapsInformation) {
    	this.metadata = mapsInformation;
    }
    
    public void setStations(List<Station> stations) {
    	this.stations = stations;
    }
    
    public void init() {
    	setBuiltInZoomControls(true);
		getController().setZoom(DETAILLED_ZOOM_LEVEL);
    }
    
    /**
	 * Hack for getting correct map bounds.
	 * 
	 * @see http://stackoverflow.com/questions/2667386/mapview-getlatitudespan-and-getlongitudespan-not-working
	 * @see http://dev.kafol.net/2011/11/android-google-maps-mapview-hacks.html  
	 */
	private Runnable waitForMapTimeTask = new Runnable() {
		public void run() {
			if (getLatitudeSpan() == 0 || getLongitudeSpan() == 360000000) {
				postDelayed(this, TIME_TO_WAIT_IN_MS);
			} else {
				try {
					new OverlaysStationsAsyncLoader().execute();
				} catch (Exception e) {
					Toast
						.makeText(getContext(), R.string.error_initialization_stations, Toast.LENGTH_LONG)
						.show();
				}
			}
		}

	};
    
	public void initOverlays() {
		Log.d(LOG_TAG, "Init overlays");
		
		if (stations == null) {
			throw new NullPointerException();
		}
		
		resetStationsOverlays();
		
		if (!mLocationEnabled) {
			// Location is disabled, center the map relative to definition in stations.xml
			GeoPoint centerPoint = new GeoPoint(metadata.getLatitude1e6(), metadata.getLongitude1e6());
			getController().setCenter(centerPoint);
		}
	}
	
	public void centerControllerAndDrawCircleOverlay() {
		resetOverlays();
		
		if (mCurrentLocation != null) {
			Log.i(LOG_TAG, "Center map and draw circle overlay");
			
			int latitudeE6 = PositionTransformer.toE6(mCurrentLocation.getLatitude());
			int longitudeE6 = PositionTransformer.toE6(mCurrentLocation.getLongitude());
			
			// Center on the current location.
			final GeoPoint locationGeoPoint = new GeoPoint(latitudeE6, longitudeE6);
			getController().setCenter(locationGeoPoint);
			
			// Draw the circle overlay.
			PositionCircleOverlay circleOverlay = new PositionCircleOverlay(latitudeE6, longitudeE6);
			getOverlays().add(circleOverlay);
		}
	}
	
	public void resetStationsOverlays() {
		mStationsOverlays = new StationsOverlays(DEFAULT_MARKER, this, getContext());
	}
	
    public void checkDelay() {
    	postDelayed(waitForMapTimeTask, TIME_TO_WAIT_IN_MS);
    }
    
    public void animateToAndUpdateOverlays(GeoPoint point) {
		getController().animateTo(point, new Runnable() {
			
			@Override
			public void run() {
				updateOverlays();
			}
		});
	}
	
	public void updateOverlays() {
		new StationsDetailsAsyncLoader().execute();
	}

	private class OverlaysStationsAsyncLoader extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			resetStationsOverlays();
			
			Log.i(LOG_TAG, "Initialize all overlays");
			
			StopWatch watch = new StopWatch();
			watch.start();
			
			for (Station eachStation : stations) {
				GeoPoint point = new GeoPoint(eachStation.getLatitudeE6(), eachStation.getLongituteE6());
				MyOverlayItem overlay = mStationsOverlays.new MyOverlayItem(point);
				overlay.updateMarker(true);
				overlay.setStation(eachStation);
				
				mStationsOverlays.addOverlay(overlay);
			}

			mStationsOverlays.populateNow();
			getOverlays().add(mStationsOverlays);
			
			watch.stop();
			Log.d(LOG_TAG, "Initialized in " + watch.getTime());
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			invalidate();
			
			Log.i(LOG_TAG, "Initialize visible stations");
			new StationsDetailsAsyncLoader().execute();
		}
		
	}

	private void resetOverlays() {
		getOverlays().clear();
	}
	
	public void updateCurrentLocation() {
		mCurrentLocation = locationManagerWrapper.getCurrentLocation();
	} 
	
	private class StationsDetailsAsyncLoader extends AsyncTask<Void, Void, Void> {

		private Loader stationsLoader = new Loader();
		
		@Override
		protected Void doInBackground(Void ... params) {
			// Only update overlays if zoom level needs to.
			if (!VlilleMapView.isDetailledZoomLevel(getZoomLevel())) {
				return null;
			}
			
			if (mStationsOverlays != null) {
				Log.d(LOG_TAG, "Update visible overlays");
				StopWatch watch = new StopWatch();
				watch.start();

				// Only load stations if station is map bounds and if is not updated more than one minute.
				final Rect mapBounds = getMapBounds();
				for (MyOverlayItem eachOverlay : mStationsOverlays.getStationsOverlay()) {
					GeoPoint point = eachOverlay.getPoint();
					boolean bounded = mapBounds.contains(point.getLongitudeE6(), point.getLatitudeE6());
					eachOverlay.updateMarker(!bounded);
					if (bounded && !eachOverlay.getStation().isUpToDate()) {
						try {
							updateDetailStation(eachOverlay);
						} catch (RuntimeException e) {
							Log.e(LOG_TAG, "doInBackground update station detail", e);
							cancel(true);
							throw new IllegalStateException("Exception occured");
						}
					}
				}
				
				postInvalidate();
				
				watch.stop();
				Log.d(LOG_TAG, "Update visible overlays in " + watch.getTime());
			}
			
			return null;
		}
		
		private void updateDetailStation(MyOverlayItem overlay) {
			Station detailledStation = stationsLoader.initSingleStation(overlay.getStation().getId());
			if (detailledStation == null) {
				throw new NullPointerException("Station is null");
			}
			
			overlay.copyDetailledStation(detailledStation);
		}
		
	}
	
	public Rect getMapBounds() {
		final GeoPoint mapCenter = getMapCenter();
		final int lngHalfSpan = getLongitudeSpan() / 2;
		final int latHalfSpan = getLatitudeSpan() / 2;
		final int drawableMarkerHeight = mStationsOverlays != null
				? mStationsOverlays.getDrawableMarkerHeight()
				: 0;

		return new Rect(
				mapCenter.getLongitudeE6() - lngHalfSpan - drawableMarkerHeight,
				mapCenter.getLatitudeE6() - latHalfSpan - drawableMarkerHeight,
				mapCenter.getLongitudeE6() + lngHalfSpan + drawableMarkerHeight,
				mapCenter.getLatitudeE6() + latHalfSpan + drawableMarkerHeight);
	}

	public static boolean isDetailledZoomLevel(int zoomLevel) {
		return zoomLevel >= DETAILLED_ZOOM_LEVEL;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			GeoPoint centerGeoPoint = this.getMapCenter();
			if (mOldCenterGeoPoint == null || (mOldCenterGeoPoint.getLatitudeE6() != centerGeoPoint.getLatitudeE6())
					|| (mOldCenterGeoPoint.getLongitudeE6() != centerGeoPoint.getLongitudeE6())) {
				mListener.onPan();
			}
			mOldCenterGeoPoint = this.getMapCenter();
		}
		
		return super.onTouchEvent(ev);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getZoomLevel() != mOldZoomLevel) {
			mListener.onZoom();
			mOldZoomLevel = getZoomLevel();
		}
	}

	public void setOnPanListener(OnPanAndZoomListener listener) {
		mListener = listener;
	}

	public boolean isLocationActivated() {
		return mLocationEnabled;
	}

	public void setLocationActivated(boolean locationActivated) {
		this.mLocationEnabled = locationActivated;
	}

	public Location getCurrentLocation() {
		return mCurrentLocation;
	}

}

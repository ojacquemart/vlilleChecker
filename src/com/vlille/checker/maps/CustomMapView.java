package com.vlille.checker.maps;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.vlille.checker.R;
import com.vlille.checker.maps.StationsOverlays.MyOverlayItem;
import com.vlille.checker.model.Station;
import com.vlille.checker.model.StationSet;
import com.vlille.checker.model.StationsMapsInformation;
import com.vlille.checker.xml.loader.StationsLoader;
import com.vlille.checker.xml.loader.StationsLoaderImpl;

/**
 * @see http://stackoverflow.com/questions/4729255/how-to-implemennt-onzoomlistener-on-mapview
 */
public class CustomMapView extends MapView {

	public static int DETAILLED_ZOOM_LEVEL = 17;
	private static final String LOG_TAG = "CustomMapView";
	private static final int TIME_TO_WAIT_IN_MS = 100;
	
    private int mOldZoomLevel = -1;
    private GeoPoint mOldCenterGeoPoint;
    private OnPanAndZoomListener mListener;
    private StationSet mStationSet;
    private StationsOverlays mStationsOverlays;

    public CustomMapView(Context context, String apiKey) {
        super(context, apiKey);
    }

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
				debug("The map is not ready");
				postDelayed(this, TIME_TO_WAIT_IN_MS);
			} else {
				new AsyncStationsLoader().execute();
			}
		}
	};	
    
    
    public void checkDelay() {
    	postDelayed(waitForMapTimeTask, TIME_TO_WAIT_IN_MS);
    }
    
    public void initOverlays(StationSet stationSet) {
    	mStationSet = stationSet;
    	
    	Drawable defaultMarker = this.getResources().getDrawable(R.drawable.station_marker);
		mStationsOverlays = new StationsOverlays(defaultMarker, this, getContext());

		initControllerDefaultValues();
    }
    
    private void initControllerDefaultValues() {
    	setBuiltInZoomControls(true);
    	
    	StationsMapsInformation mapsInformation = mStationSet.getMapsInformations();
    	
    	GeoPoint centerPoint = new GeoPoint(mapsInformation.getLatitude1e6(), mapsInformation.getLongitude1e6());
    	final MapController controller = getController();
    	controller.setCenter(centerPoint);
    	controller.setZoom(CustomMapView.DETAILLED_ZOOM_LEVEL);
    }
	
	private class AsyncStationsLoader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			getOverlays().clear();
			
			debug("Initialize all overlays");
			
			final List<Station> stations = mStationSet.getStations();
			for (Station eachStation : stations) {
				GeoPoint point = new GeoPoint(eachStation.getLatitude1e6(), eachStation.getLongitute1e6());
				MyOverlayItem overlay = mStationsOverlays.new MyOverlayItem(point);
				overlay.updateMarker(true);
				overlay.setStation(eachStation);
				
				mStationsOverlays.addOverlay(overlay);
			}

			mStationsOverlays.populateNow();
			getOverlays().add(mStationsOverlays);
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			debug("Initialize visible stations");
			new AsyncDetailStationLoader().execute();
			
			invalidate();
		}
		
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
		new AsyncDetailStationLoader().execute();
	}
	
	private class AsyncDetailStationLoader extends AsyncTask<Void, Void, Void> {

		private StationsLoader stationsLoader = new StationsLoaderImpl();
		
		@Override
		protected Void doInBackground(Void ... params) {
			// Only update overlays if zoom level needs to.
			if (!CustomMapView.isDetailledZoomLevel(getZoomLevel())) {
				return null;
			}

			// Only load stations if station is map bounds and if is not updated more than one minute.
			Rect mapBounds = getMapBounds();
			for (MyOverlayItem eachOverlay : mStationsOverlays.getStationsOverlay()) {
				GeoPoint point = eachOverlay.getPoint();
				boolean bounded = mapBounds.contains(point.getLongitudeE6(), point.getLatitudeE6());
				eachOverlay.updateMarker(!bounded);

				if (bounded && !eachOverlay.isUpToDate()) {
					debug("update overlay");
					
					try {
						updateDetailStation(eachOverlay);
					} catch (RuntimeException e) {
						throw e;
					}
				}
			}
		
			postInvalidate();
			
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
		final int drawableMarkerHeight = mStationsOverlays.getDrawableMarkerHeight();

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
	
	private void debug(String text) {
    	Log.d(LOG_TAG, text);
    }

	public void setOnPanListener(OnPanAndZoomListener listener) {
		mListener = listener;
	}


}

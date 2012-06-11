package com.vlille.checker.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.vlille.checker.R;
import com.vlille.checker.maps.overlay.PositionCircleOverlay;
import com.vlille.checker.maps.overlay.BallonStationOverlays;
import com.vlille.checker.maps.overlay.BallonStationOverlays.StationDetails;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.Station;

/**
 * @see http://stackoverflow.com/questions/4729255/how-to-implemennt-onzoomlistener-on-mapview
 */
public class VlilleMapView extends MapView {

	private static final int DETAILLED_ZOOM_LEVEL = 17;
	
	private final String LOG_TAG = getClass().getSimpleName();
	private final Drawable DEFAULT_MARKER = getResources().getDrawable(R.drawable.station_marker);
	
	private LocationManagerWrapper locationManagerWrapper = new LocationManagerWrapper(getContext());
	private boolean mLocationEnabled;
    private int mOldZoomLevel = -1;
    private Location mCurrentLocation = null;
    
    private GeoPoint mOldCenterGeoPoint;
    private OnPanAndZoomListener mListener;
    
    private Metadata metadata;
    private BallonStationOverlays mStationsOverlays;
    
    /**
     * Store a map with each station overlay by station id.
     */
	private Map<String, StationDetails> mapOverlaysByStationId = new HashMap<String, StationDetails>();

    public VlilleMapView(Context context, String apiKey) {
        super(context, apiKey);
        initDefaultZoom();
    }

    public VlilleMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefaultZoom();
    }

    public VlilleMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDefaultZoom();
    }
    
    public void setMapsInformations(Metadata mapsInformation) {
    	this.metadata = mapsInformation;
    }
    
    private void initDefaultZoom() {
    	setBuiltInZoomControls(true);
		getController().setZoom(DETAILLED_ZOOM_LEVEL);
    }
    
    public void createOverlays(List<Station> stations) {
		resetStationsOverlays();
		
		Log.i(LOG_TAG, "Initialize all overlays");
		
		StopWatch watch = new StopWatch();
		watch.start();
		
		for (Station eachStation : stations) {
			GeoPoint point = new GeoPoint(eachStation.getLatitudeE6(), eachStation.getLongituteE6());
			StationDetails overlay = mStationsOverlays.createNewOverlay(point, eachStation);
			
			mapOverlaysByStationId.put(eachStation.getId(), overlay);
		}

		getOverlays().add(mStationsOverlays);
		mStationsOverlays.populateNow();
		
		watch.stop();
		Log.d(LOG_TAG, "Initialized in " + watch.getTime());
	}    
    
	public void initCenter() {
		Log.d(LOG_TAG, "#initCenter");
		
		if (!mLocationEnabled) {
			defaultCenter();
		} else {
			locationCenter();
		}
	}

	private void defaultCenter() {
		center(new GeoPoint(metadata.getLatitude1e6(), metadata.getLongitude1e6()));
	}
	
	public void locationCenter() {
		clearOverlays();
		
		Log.d(LOG_TAG, "Current location not null : " + (mCurrentLocation != null));
		if (mCurrentLocation != null) {
			Log.i(LOG_TAG, "Center map and draw circle overlay");
			
			int latitudeE6 = PositionTransformer.toE6(mCurrentLocation.getLatitude());
			int longitudeE6 = PositionTransformer.toE6(mCurrentLocation.getLongitude());
			
			// Center on the current location.
			center(new GeoPoint(latitudeE6, longitudeE6));
			
			// Draw the circle overlay.
			PositionCircleOverlay circleOverlay = new PositionCircleOverlay(latitudeE6, longitudeE6);
			getOverlays().add(circleOverlay);
		}
	}
	
	private void center(GeoPoint point) {
		getController().setCenter(point);
	}
	
	public void animateTo(GeoPoint point) {
		getController().animateTo(point);
	}
	
	public void resetStationsOverlays() {
		mStationsOverlays = new BallonStationOverlays(DEFAULT_MARKER, this, getContext());
	}
	
	public StationDetails getOverlayByStationId(Station station) {
		if (station == null) {
			return null;
		}
		
		return mapOverlaysByStationId.get(station.getId());
	}
	
	private void clearOverlays() {
		getOverlays().clear();
	}
	
	public void updateCurrentLocation() {
		mCurrentLocation = locationManagerWrapper.getCurrentLocation();
	}
	
	public List<StationDetails> getAllOverlays() {
		return mStationsOverlays.getStationsOverlay();
	}
	
	public List<Station> getBoundedStations() {
		if (!canShowDetails()) {
			return new ArrayList<Station>();
		}
		
		Log.d(LOG_TAG, "Update visible overlays");
		StopWatch watch = new StopWatch();
		watch.start();
		
		List<Station> overlays = new ArrayList<Station>();
		
		// Only load stations if station is map bounds and if is not updated more than one minute.
		final Rect mapBounds = getMapBounds();
		for (StationDetails eachOverlay : mStationsOverlays.getStationsOverlay()) {
			GeoPoint point = eachOverlay.getPoint();
			boolean bounded = mapBounds.contains(point.getLongitudeE6(), point.getLatitudeE6());
			eachOverlay.setMarkerPin(!bounded);
			
			if (bounded) {
				overlays.add(eachOverlay.getStation());
			}
		}
		
		return overlays;
	}
	

	/**
	 * Only show overlays details when <code>{@link #mStationsOverlays}</code> is not null and zoom level allows to.
	 * @return <code>true </code> if maps can show details, <code>false</code> otherwise.
	 */
	private boolean canShowDetails() {
		return mStationsOverlays != null && VlilleMapView.isDetailledZoomLevel(getZoomLevel());
	}
	
	/**
	 * Get map bounds according to the screen size.
	 * @return the map bounds.
	 */
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
	
	public Map<String, StationDetails> getMapOverlaysByStationId() {
		return mapOverlaysByStationId;
	}
	

}

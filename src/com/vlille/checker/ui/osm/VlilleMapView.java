package com.vlille.checker.ui.osm;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.async.AbstractAsyncStationTask;
import com.vlille.checker.ui.osm.location.LocationManagerWrapper;
import com.vlille.checker.ui.osm.overlay.BubbleInfoWindow;
import com.vlille.checker.ui.osm.overlay.CircleLocationOverlay;
import com.vlille.checker.ui.osm.overlay.ItemizedOverlayWithFocus;
import com.vlille.checker.ui.osm.overlay.ItemizedOverlayWithFocus.OverlayZoomUtils;
import com.vlille.checker.ui.osm.overlay.MaskableOverlayItem;
import com.vlille.checker.ui.osm.overlay.ResourceProxyImpl;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.StationUtils;
import com.vlille.checker.utils.ToastUtils;

/**
 * @see http://stackoverflow.com/questions/4729255/how-to-implemennt-onzoomlistener-on-mapview
 */
public class VlilleMapView extends MapView implements LocationListener {

	public static final int DEFAULT_ZOOM_LEVEL = 12;
	
	private final String TAG = getClass().getSimpleName();
	
	private MapState state;
	private List<Station> stations;

	private SherlockFragmentActivity sherlockActivity;
	private boolean locationOn;
	private CircleLocationOverlay circleOverlay;
	private ItemizedOverlayWithFocus<MaskableOverlayItem> itemizedOverlay;
	
	public VlilleMapView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "VlilleMapView");
	}
	
	public void setMapInfos(MapState state, List<Station> stations) {
		this.state = state;
		this.stations = stations;
	}
	
	public void init() {
		initConfiguration();
		initCenter();
		initCircleOverlay();
		initIconizedOverlay();
		
		setOnPanZoomListener();
		invalidate();
	}

	private void initConfiguration() {
		setTileSource(TileSourceFactory.MAPNIK);
		setBuiltInZoomControls(true);
		setMultiTouchControls(true);
	}

	private void initCenter() {
		GeoPoint center = new GeoPoint(state.currentCenter);
		
		MapController mMapController = getController();
		mMapController.setZoom(state.zoomLevel);
		mMapController.setCenter(center);
	}
	
	//=========
	// Location
	//=========
	
	/**
	 * Switch location flag and draw the circle with stations around if location is on.
	 * TODO: hide stations not around the current geoPoint.
	 */
	public void updateLocationCircle() {
		this.locationOn = !locationOn;
		Log.d(TAG, "Location on: " + locationOn);
		if (locationOn) {
			requestLocationUpdates();
			drawLocationCircle();
		} else {
			getLocationManager().removeUpdates(this);
			circleOverlay.setGeoPosition(null);
			updateStations();
		}
		
	}
		
	private void requestLocationUpdates() {
		final LocationManager locationManager = getLocationManager();
		final List<String> providers = locationManager.getProviders(false);
		for (String eachProviderName : providers) {
			Log.d(TAG, "Provider enabled " + eachProviderName);
			locationManager.requestLocationUpdates(eachProviderName,
					LocationManagerWrapper.DISTANCE_UPDATE_IN_METERS,
					LocationManagerWrapper.DURATION_UPDATE_IN_MILLIS,
					this);
		}
	}

	private LocationManager getLocationManager() {
		return (LocationManager) sherlockActivity.getSystemService(Context.LOCATION_SERVICE);
	}
	
	private void drawLocationCircle() {
		if (locationOn) {
			if (LocationManagerWrapper.with(getContext()).hasCurrentLocation()) {
				updateStations();
			} else {
				ToastUtils.show(getContext(), R.string.error_no_location_found);
			}
		}
	}
	
	private void initCircleOverlay() {
		circleOverlay = new CircleLocationOverlay(getContext());
		getOverlays().add(0, circleOverlay);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged");
		drawLocationCircle();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "onProviderEnabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "onStatusChanged");
	}
	
	//=========
	// Overlays
	//=========

	private void initIconizedOverlay() {
		final List<MaskableOverlayItem> items = initOverlays();
		
		final ResourceProxy mResourceProxy = new ResourceProxyImpl(getContext());
		final Resources resources = getResources();
		itemizedOverlay = new ItemizedOverlayWithFocus<MaskableOverlayItem>(
				items,
				resources,
				new BubbleInfoWindow(R.layout.maps_bubble, this),
				new ItemizedIconOverlay.OnItemGestureListener<MaskableOverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final MaskableOverlayItem item) {
						return false;
					}

					@Override
					public boolean onItemLongPress(final int index, final MaskableOverlayItem item) {
						return false;
					}
				}, mResourceProxy);
		itemizedOverlay.setFocusItemsOnTap(true);

		getOverlays().add(itemizedOverlay);
	}

	private List<MaskableOverlayItem> initOverlays() {
		final List<MaskableOverlayItem> items = new ArrayList<MaskableOverlayItem>();
		
		for (Station eachStation : stations) {
			final MaskableOverlayItem extendedOverlayItem = new MaskableOverlayItem(
					eachStation.getName(), eachStation.getName(),
					eachStation.getGeoPoint(),
					getContext());
			extendedOverlayItem.setRelatedObject(eachStation);
			
			items.add(extendedOverlayItem);
		}
		
		return items;
	}

	private void setOnPanZoomListener() {
		setOnPanListener(new OnPanAndZoomListener() {

			@Override
			public void onZoom() {
				Log.d(TAG, "onZoom");
				updateStations();
			}

			@Override
			public void onPan() {
				Log.d(TAG, "onPan");
				updateStations();
			}

		});
	}
	
	public void updateStations() {
		if (!ContextHelper.isNetworkAvailable(getContext())) {
			return;
		}
		
		final List<Station> stations = new ArrayList<Station>();
		
		final ItemActionUpdater itemUpdater = getItemUpdater();
		if (itemUpdater.isValid()) {
			for (MaskableOverlayItem eachItem : itemizedOverlay.getItems()) {
				final Station relatedStation = (Station) eachItem.getRelatedObject();
				if (itemUpdater.canUpdate(eachItem, relatedStation)) {
					stations.add(relatedStation);
				}
			}
		}
		
		if (!stations.isEmpty()) {
			Log.d(TAG, "" + stations.size() + "stations to update!");
			new AsyncMapStationRetriever().execute(stations);
			itemUpdater.whenDraw();
		} else {
			itemUpdater.whenNotDraw();
			// Some stations may have seen their visibility attribute changed.
			invalidate();
		}
	}

	private ItemActionUpdater getItemUpdater() {
		if (locationOn && LocationManagerWrapper.with(getContext()).hasCurrentLocation()) {
			return getLocationItemUpdater();
		}
		
		return getClassicItemUpdater();
	}
	
	/**
	 * The classic update just make the station visible if the
	 * station is displayed in current screen and if the zoom
	 * level allows it.
	 * 
	 * @return the classic updater.
	 * @see OverlayZoomUtils#isDetailledZoomLevel(int)
	 * @see MapView#getBoundingBox()
	 */
	private ItemActionUpdater getClassicItemUpdater() {
		final BoundingBoxE6 boundingBox = getBoundingBox();
		final int zoomLevel = getZoomLevel();
		
		return new ItemActionUpdater() {

			@Override
			public boolean isValid() {
				return true;
			}
			
			public boolean canUpdate(MaskableOverlayItem item, Station station) {
				item.setVisible(true);
				
				return OverlayZoomUtils.isDetailledZoomLevel(zoomLevel) &&
						boundingBox.contains(station.getGeoPoint());
						
			}
			
			public void whenDraw() {}
			
			public void whenNotDraw() {}

		};
	}
	
	/**
	 * The location updater checks if the current location exists and
	 * if the station is near the current location. Then the station
	 * will be displayed only if it is near the current position.
	 * 
	 * @return the location updater.
	 */
	private ItemActionUpdater getLocationItemUpdater() {
		final long radiusValue = ContextHelper.getRadiusValue(getContext());
		final Location currentLocation = LocationManagerWrapper.with(getContext()).getCurrentLocation();
		
		return new ItemActionUpdater() {
			
			@Override
			public boolean isValid() {
				return currentLocation != null;
			}
			
			@Override
			public boolean canUpdate(MaskableOverlayItem item, Station station) {
				final boolean nearCurrentLocation = StationUtils.isNearCurrentLocation(station, currentLocation, radiusValue);
				item.setVisible(nearCurrentLocation);
				
				return nearCurrentLocation;
			}
			
			public void whenDraw() {
				Log.d(TAG, "Current location [lat=" + currentLocation.getLatitude() + ",long=" + currentLocation.getLongitude() + "]");
				final GeoPoint geoPoint = new GeoPoint(currentLocation);
				getController().setCenter(geoPoint);
				circleOverlay.setGeoPosition(geoPoint);
			}
			
			public void whenNotDraw() {
				ToastUtils.show(getContext(), R.string.error_no_stations_near_current_location);
			}
		};
	}
	
	class AsyncMapStationRetriever extends AbstractAsyncStationTask {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			sherlockActivity.setProgressBarIndeterminateVisibility(true);
		}
		
		@Override
		protected void onPostExecute(List<Station> result) {
			super.onPostExecute(result);
			sherlockActivity.setProgressBarIndeterminateVisibility(false);
			invalidate();
		}
	}

	// public void initCenter() {
	// Log.d(LOG_TAG, "#initCenter");
	//
	// if (!locationEnabled) {
	// defaultCenter();
	// } else {
	// locationCenter();
	// }
	// }

	// private void defaultCenter() {
	// center(new GeoPoint(metadata.getLatitude1e6(), metadata.getLongitude1e6()));
	// }
	//
	// public void locationCenter() {
	// clearOverlays();
	//
	// Log.d(LOG_TAG, "Current location not null : " + (currentLocation != null));
	// if (currentLocation != null) {
	// Log.i(LOG_TAG, "Center map and draw circle overlay");
	//
	// int latitudeE6 = PositionTransformer.toE6(currentLocation.getLatitude());
	// int longitudeE6 = PositionTransformer.toE6(currentLocation.getLongitude());
	//
	// // Center on the current location.
	// center(new org.osmdroid.util.GeoPoint(latitudeE6, longitudeE6));
	//
	// // Draw the circle overlay.
	// // PositionCircleOverlay circleOverlay = new PositionCircleOverlay(latitudeE6, longitudeE6);
	// // getOverlays().add(circleOverlay);
	// }
	// }
	//

	//=====================
	// onZoomAndPanListener
	//=====================
	
	private int oldZoomLevel = -1;
	private GeoPoint oldCenterGeoPoint;
	private OnPanAndZoomListener panAndZoomListener;
	
	/**
	 * Detects move on touch event in order to refresh station, and retrieves the new value of the map center.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			GeoPoint centerGeoPoint = (GeoPoint) this.getMapCenter();
			if (oldCenterGeoPoint == null || (oldCenterGeoPoint.getLatitudeE6() != centerGeoPoint.getLatitudeE6())
					|| (oldCenterGeoPoint.getLongitudeE6() != centerGeoPoint.getLongitudeE6())) {
				panAndZoomListener.onPan();
			}
			oldCenterGeoPoint = (GeoPoint) this.getMapCenter();
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getZoomLevel() != oldZoomLevel) {
			panAndZoomListener.onZoom();
			oldZoomLevel = getZoomLevel();
		}
	}

	public void setOnPanListener(OnPanAndZoomListener listener) {
		panAndZoomListener = listener;
	}

	public void setSherlockActivity(SherlockFragmentActivity sherlockActivity) {
		this.sherlockActivity = sherlockActivity;
	}

}

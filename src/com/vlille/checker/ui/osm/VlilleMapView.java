package com.vlille.checker.ui.osm;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
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
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.async.AbstractAsyncStationTask;
import com.vlille.checker.ui.osm.location.LocationManagerWrapper;
import com.vlille.checker.ui.osm.overlay.BubbleInfoWindow;
import com.vlille.checker.ui.osm.overlay.CircleOverlay;
import com.vlille.checker.ui.osm.overlay.ItemizedOverlayWithFocus;
import com.vlille.checker.ui.osm.overlay.ItemizedOverlayWithFocus.OverlayZoomUtils;
import com.vlille.checker.ui.osm.overlay.ResourceProxyImpl;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.StationUtils;

/**
 * @see http://stackoverflow.com/questions/4729255/how-to-implemennt-onzoomlistener-on-mapview
 */
public class VlilleMapView extends MapView implements LocationListener {

	private static final int DEFAULT_ZOOM_LEVEL = 13;
	
	private final String TAG = getClass().getSimpleName();

	// Metadata infos.
	private Metadata metadata;

	private SherlockFragmentActivity sherlockActivity;
	private boolean locationOn;
	private CircleOverlay circleOverlay;
	private ItemizedOverlayWithFocus<ExtendedOverlayItem> itemizedOverlay;
	
	public VlilleMapView(final Context context, AttributeSet attrs) {
		super(context, attrs);

		initConfiguration();
		initCenter();
		initCircleOverlay();
		initIconizedOverlay();
		
		setOnPanZoomListener();
	}

	private void initConfiguration() {
		setTileSource(TileSourceFactory.MAPNIK);
		setBuiltInZoomControls(true);
		setMultiTouchControls(true);
	}

	private void initCenter() {
		// TODO: change default center.
		GeoPoint center = new GeoPoint(PositionTransformer.toE6(50.6419), PositionTransformer.toE6(3.1));
		
		MapController mMapController = getController();
		mMapController.setZoom(DEFAULT_ZOOM_LEVEL);
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
		} else {
			final LocationManager locationManager = getLocationManager();
			locationManager.removeUpdates(this);
			circleOverlay.setGeoPosition(null);
			invalidate();
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
		GeoPoint geoPoint = null;
		if (locationOn) {
			geoPoint = LocationManagerWrapper.with(getContext()).getCurrentGeoPoint();
		}
		
		circleOverlay.setGeoPosition(geoPoint);
		
		invalidate();
		
	}
	
	private void initCircleOverlay() {
		circleOverlay = new CircleOverlay(getContext());
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
		final List<ExtendedOverlayItem> items = initOverlays();
		
		final ResourceProxy mResourceProxy = new ResourceProxyImpl(getContext());
		final Resources resources = getResources();
		itemizedOverlay = new ItemizedOverlayWithFocus<ExtendedOverlayItem>(
				items,
				resources,
				new BubbleInfoWindow(R.layout.maps_bubble, this),
				new ItemizedIconOverlay.OnItemGestureListener<ExtendedOverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final ExtendedOverlayItem item) {
						return false;
					}

					@Override
					public boolean onItemLongPress(final int index, final ExtendedOverlayItem item) {
						return false;
					}
				}, mResourceProxy);
		itemizedOverlay.setFocusItemsOnTap(true);

		getOverlays().add(itemizedOverlay);
	}

	private List<ExtendedOverlayItem> initOverlays() {
		final List<ExtendedOverlayItem> items = new ArrayList<ExtendedOverlayItem>();
		
		final List<Station> stations = VlilleChecker.getDbAdapter().findAll();
		for (Station eachStation : stations) {
			final ExtendedOverlayItem extendedOverlayItem = new ExtendedOverlayItem(
					eachStation.getName(), eachStation.getName(),
					eachStation.getPoint(),
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
		final List<Station> stations = new ArrayList<Station>();
		
		final ItemUpdater itemUpdater = getItemUpdater();
		if (itemUpdater.isValid()) {
			for (ExtendedOverlayItem eachItem : itemizedOverlay.getItems()) {
				final Station relatedStation = (Station) eachItem.getRelatedObject();
				if (itemUpdater.canUpdate(relatedStation)) {
					stations.add(relatedStation);
				}
			}
		}
		
		if (!stations.isEmpty()) {
			Log.d(TAG, "" + stations.size() + "stations to update!");
			new AsyncMapStationRetriever().execute(stations);
		}
	}

	private ItemUpdater getItemUpdater() {
		if (locationOn) {
			return getLocationItemUpdater();
		}
		
		return getClassicItemUpdater();
	}
	
	private ItemUpdater getClassicItemUpdater() {
		final BoundingBoxE6 boundingBox = getBoundingBox();
		final int zoomLevel = getZoomLevel();
		
		return new ItemUpdater() {

			@Override
			public boolean isValid() {
				return true;
			}
			
			public boolean canUpdate(Station station) {
				return OverlayZoomUtils.isDetailledZoomLevel(zoomLevel) &&
						boundingBox.contains(station.getPoint());
						
			}

		};
	}
	
	private ItemUpdater getLocationItemUpdater() {
		final long radiusValue = ContextHelper.getRadiusValue(getContext());
		final Location currentLocation = LocationManagerWrapper.with(getContext()).getCurrentLocation();
		
		return new ItemUpdater() {
			
			@Override
			public boolean isValid() {
				return currentLocation != null;
			}
			
			@Override
			public boolean canUpdate(Station station) {
				return StationUtils.isNearCurrentLocation(station, currentLocation, radiusValue);
			}
		};
	}
	
	class AsyncMapStationRetriever extends AbstractAsyncStationTask {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d(TAG, "onPreExecute");
			sherlockActivity.setProgressBarIndeterminateVisibility(true);
		}
		
		@Override
		protected void onPostExecute(List<Station> result) {
			super.onPostExecute(result);
			Log.d(TAG, "onPostExecute");
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

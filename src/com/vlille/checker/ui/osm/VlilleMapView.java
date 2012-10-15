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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.async.AbstractAsyncStationTask;
import com.vlille.checker.ui.osm.ItemizedOverlayWithFocus.OverlayZoomUtils;

/**
 * @see http://stackoverflow.com/questions/4729255/how-to-implemennt-onzoomlistener-on-mapview
 */
public class VlilleMapView extends MapView {

	private final String TAG = getClass().getSimpleName();

	private static final int DEFAULT_ZOOM_LEVEL = 13;

	private int oldZoomLevel = -1;

	private LocationManagerWrapper locationManagerWrapper = new LocationManagerWrapper(getContext());
	private boolean locationEnabled;
	private Location currentLocation = null;

	private GeoPoint oldCenterGeoPoint;
	private OnPanAndZoomListener panAndZoomListener;

	// Metadata infos.
	private Metadata metadata;

	private SherlockFragmentActivity sherlockActivity;
	private ItemizedOverlayWithFocus<ExtendedOverlayItem> itemizedOverlay;
	
	public VlilleMapView(final Context context, AttributeSet attrs) {
		super(context, attrs);

		initConfiguration();
		initCenter();
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
				updateVisibleStations();
			}

			@Override
			public void onPan() {
				Log.d(TAG, "onPan");
				updateVisibleStations();
			}

		});
	}
	
	public void updateVisibleStations() {
		final List<Station> stations = new ArrayList<Station>();
		final int zoomLevel = getZoomLevel();
		final BoundingBoxE6 boundingBox = getBoundingBox();
		for (ExtendedOverlayItem eachItem : itemizedOverlay.getItems()) {
			if (isVisibleAndEnoughZoomLevel(boundingBox, eachItem.getPoint(), zoomLevel)) {
				stations.add((Station) eachItem.getRelatedObject());
			}
		}
		
		Log.d(TAG, "" + stations.size() + " to update!");
		new AsyncMapStationRetriever().execute(stations);
	}
	
	private boolean isVisibleAndEnoughZoomLevel(BoundingBoxE6 boundingBox, GeoPoint geoPoint, int zoomLevel) {
		return boundingBox.contains(geoPoint) && OverlayZoomUtils.isDetailledZoomLevel(zoomLevel);
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
	
	// onZoomAndPanListener

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

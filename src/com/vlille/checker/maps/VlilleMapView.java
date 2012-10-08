package com.vlille.checker.maps;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.maps.overlay.ItemizedOverlayWithFocus;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.Station;

/**
 * @see http://stackoverflow.com/questions/4729255/how-to-implemennt-onzoomlistener-on-mapview
 */
public class VlilleMapView extends org.osmdroid.views.MapView implements MapListener {

	private final String LOG_TAG = getClass().getSimpleName();

	// Margin to pre load some stations.
	private static final double MAP_VIEW_BOUNDS_MARGIN = 0.60;

	// Detailled zoom level in which details are displayed.
	private static final int DETAILLED_ZOOM_LEVEL = 17;

	// Default station marker.
	private final Drawable DEFAULT_MARKER = getResources().getDrawable(R.drawable.station_marker);

	private int oldZoomLevel = -1;

	private LocationManagerWrapper locationManagerWrapper = new LocationManagerWrapper(getContext());
	private boolean locationEnabled;
	private Location currentLocation = null;

	private GeoPoint oldCenterGeoPoint;
	private OnPanAndZoomListener panAndZoomListener;

	// Metadata infos.
	private Metadata metadata;

	private Activity activity;

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	private PopupPanel panel;

	// Ballon stations overlays.
	// private BallonStationOverlays ballonStationsOverlays;
	//
	// // Store a map with each station overlay by station id.
	// private Map<String, StationDetails> mapOverlaysByStationId =m new HashMap<String, StationDetails>();

	// TODO: http://bricolsoftconsulting.com/2011/10/31/extending-mapview-to-add-a-change-event/
	public VlilleMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		panel = new PopupPanel(R.layout.popup);
		MapController mMapController = getController();
		mMapController.setZoom(DETAILLED_ZOOM_LEVEL);
		GeoPoint gPt = new GeoPoint(PositionTransformer.toE6(50.6419), PositionTransformer.toE6(3.1));
		// Centre map near to Hyde Park Corner, London
		mMapController.setCenter(gPt);

		setTileSource(TileSourceFactory.MAPNIK);
		setBuiltInZoomControls(true);
		setMultiTouchControls(true);
		setMapListener(this);
		
		// TODO: refactor onDrawnAt to handle new zoomed drawer!
		// TODO: check api to use balloon shit!

		final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		final List<Station> stations = VlilleChecker.getDbAdapter().findAll();
		for (Station eachStation : stations) {
			items.add(new OverlayItem(eachStation.getName(), "", eachStation.getPoint()));
		}

		final ResourceProxy mResourceProxy = new ResourceProxyImpl(getContext());

		ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
		final Resources resources = getResources();
		mMyLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
				resources.getDrawable(R.drawable.station_pin),
				resources.getDrawable(R.drawable.station_marker),
				resources.getDimensionPixelSize(R.dimen.overlay_font_size),
				NOT_SET,
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
						getController().animateTo(item.getPoint(), new Runnable() {

							@Override
							public void run() {
							}
						}, new Runnable() {

							@Override
							public void run() {
								panel.show(true);
							}
						});
						return true;
					}

					@Override
					public boolean onItemLongPress(final int index, final OverlayItem item) {
						return false;
					}
				}, mResourceProxy);
		mMyLocationOverlay.setFocusItemsOnTap(true);

		getOverlays().add(mMyLocationOverlay);

		// this.equipementsOverlay = new EquipementsItemizedOverlay(context,
		// parkingMarker, this);
		//
		// // On ajoute toujours l'overlay avec les équipements
		// addOverlay(this.equipementsOverlay);
		//
		// // création de l'overlay de la circulation
		// this.segmentsOverlay = new SegmentItemizedOvlerlay();
		// SegmentsAsyncTask segmentLoader = new SegmentsAsyncTask(context, this);
		// MainApplication.executor.execute(segmentLoader.future());
	}

	class PopupPanel {
		View popup;
		boolean isVisible = false;

		PopupPanel(int layout) {
			ViewGroup parent = (ViewGroup) getParent();

			final LayoutInflater inflater = LayoutInflater.from(getContext());
			popup = inflater.inflate(layout, parent, false);
			popup.setVisibility(View.GONE);

			popup.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					hide();
				}
			});
			popup.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					System.out.println("focusChange " + hasFocus);

				}
			});
		}

		View getView() {
			return (popup);
		}

		void show(boolean alignTop) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( //
					RelativeLayout.LayoutParams.WRAP_CONTENT, //
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.ABOVE);
			lp.setMargins((getWidth() / 2) - 50, getHeight() / 2 - 200, 0, 0);

			hide();

			((ViewGroup) getParent()).addView(popup, lp);
			isVisible = true;
			popup.setVisibility(View.VISIBLE);
		}
		
		void hide() {
			popup.setVisibility(View.GONE);
			if (isVisible) {
				isVisible = false;
				((ViewGroup) popup.getParent()).removeView(popup);
			}
		}
	}

	// public void setMapsInformations(Metadata mapsInformation) {
	// this.metadata = mapsInformation;
	// }

	// public void createOverlays(List<Station> stations) {
	// resetStationsOverlays();

	// Log.i(LOG_TAG, "Initialize all overlays");
	//
	// StopWatch watch = new StopWatch();
	// watch.start();

	// for (Station eachStation : stations) {
	// GeoPoint point = new GeoPoint(eachStation.getLatitudeE6(), eachStation.getLongituteE6());
	// StationDetails overlay = ballonStationsOverlays.createNewOverlay(point, eachStation);
	//
	// mapOverlaysByStationId.put(eachStation.getId(), overlay);
	// }
	//
	// getOverlays().add(ballonStationsOverlays);
	// ballonStationsOverlays.populateNow();

	// watch.stop();
	// Log.d(LOG_TAG, "Initialized in " + watch.getTime());
	// }

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
	// private void center(org.osmdroid.util.GeoPoint point) {
	// getController().setCenter(point);
	// }
	//
	// public void animateTo(org.osmdroid.util.GeoPoint point) {
	// getController().animateTo(point);
	// }

	// public void resetStationsOverlays() {
	// ballonStationsOverlays = new BallonStationOverlays(DEFAULT_MARKER, this, getContext());
	// }
	//
	// public StationDetails getOverlayByStationId(Station station) {
	// if (station == null) {
	// return null;
	// }
	//
	// return mapOverlaysByStationId.get(station.getId());
	// }

	// private void clearOverlays() {
	// getOverlays().clear();
	// }
	//
	// public void updateCurrentLocation() {
	// currentLocation = locationManagerWrapper.getCurrentLocation();
	// }

	// public List<Station> getBoundedStations() {
	// if (!canShowDetails()) {
	// return new ArrayList<Station>();
	// }
	//
	// List<Station> overlays = new ArrayList<Station>();
	//
	// // Only load stations if station is map bounds and if is not updated more than one minute.
	// final Rect mapBounds = getMapBoundsRect();
	// for (StationDetails eachOverlay : ballonStationsOverlays.getStationsOverlay()) {
	// GeoPoint point = eachOverlay.getPoint();
	// boolean bounded = mapBounds.contains(point.getLongitudeE6(), point.getLatitudeE6());
	// eachOverlay.setMarkerPin(!bounded);
	//
	// if (bounded) {
	// overlays.add(eachOverlay.getStation());
	// }
	// }
	//
	// return overlays;
	// }

	/**
	 * Only show overlays details when <code>{@link #ballonStationsOverlays}</code> is not null and zoom level allows
	 * to.
	 * 
	 * @return <code>true </code> if maps can show details, <code>false</code> otherwise.
	 */
	// private boolean canShowDetails() {
	// return ballonStationsOverlays != null && VlilleMapView.isDetailledZoomLevel(getZoomLevel());
	// }

	/**
	 * Get map bounds according to the screen size.
	 * 
	 * @return the map bounds.
	 */
	// public Rect getMapBoundsRect() {
	// final GeoPoint mapCenter = (GeoPoint) getMapCenter();
	//
	// final double width = getLongitudeSpan() * MAP_VIEW_BOUNDS_MARGIN;
	// final double height = getLatitudeSpan() * MAP_VIEW_BOUNDS_MARGIN;
	//
	// int drawableMarkerHeight = 0;
	// if (ballonStationsOverlays != null) {
	// drawableMarkerHeight = ballonStationsOverlays.getDrawableMarkerHeight();
	// }
	//
	// return new Rect(
	// mapCenter.getLongitudeE6() - (int) width - drawableMarkerHeight,
	// mapCenter.getLatitudeE6() - (int) height - drawableMarkerHeight,
	// mapCenter.getLongitudeE6() + (int) width + drawableMarkerHeight,
	// mapCenter.getLatitudeE6() + (int) height + drawableMarkerHeight);
	// }

	public static boolean isDetailledZoomLevel(int zoomLevel) {
		return zoomLevel >= DETAILLED_ZOOM_LEVEL;
	}

	@Override
	public boolean onScroll(ScrollEvent arg0) {
		return false;
	}

	@Override
	public boolean onZoom(ZoomEvent arg0) {
		return false;
	}

	/**
	 * Detects move on touch event in order to refresh station, and retrieves the new value of the map center.
	 */
	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// if (event.getAction() == MotionEvent.ACTION_UP) {
	// GeoPoint centerGeoPoint = (GeoPoint) this.getMapCenter();
	// if (oldCenterGeoPoint == null || (oldCenterGeoPoint.getLatitudeE6() != centerGeoPoint.getLatitudeE6())
	// || (oldCenterGeoPoint.getLongitudeE6() != centerGeoPoint.getLongitudeE6())) {
	// panAndZoomListener.onPan();
	// }
	// oldCenterGeoPoint = (GeoPoint) this.getMapCenter();
	// }
	//
	// return super.onTouchEvent(event);
	// }

	// @Override
	// protected void dispatchDraw(Canvas canvas) {
	// super.dispatchDraw(canvas);
	// if (getZoomLevel() != oldZoomLevel) {
	// panAndZoomListener.onZoom();
	// oldZoomLevel = getZoomLevel();
	// }
	// }
	//
	// public void setOnPanListener(OnPanAndZoomListener listener) {
	// panAndZoomListener = listener;
	// }
	//
	// public boolean isLocationActivated() {
	// return locationEnabled;
	// }
	//
	// public void setLocationActivated(boolean locationActivated) {
	// this.locationEnabled = locationActivated;
	// }
	//
	// public Location getCurrentLocation() {
	// return currentLocation;
	// }

	// public Map<String, StationDetails> getMapOverlaysByStationId() {
	// return mapOverlaysByStationId;
	// }

}

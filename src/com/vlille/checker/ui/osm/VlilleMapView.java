package com.vlille.checker.ui.osm;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ToastUtils;

/**
 * @see http://stackoverflow.com/questions/4729255/how-to-implemennt-onzoomlistener-on-mapview
 */
public class VlilleMapView extends org.osmdroid.views.MapView implements MapListener {

	private final String TAG = getClass().getSimpleName();

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
	private PopupPanel panel;
	private ItemizedOverlayWithFocus<ExtendedOverlayItem> mMyLocationOverlay;
	
	// TODO: http://bricolsoftconsulting.com/2011/10/31/extending-mapview-to-add-a-change-event/
	public VlilleMapView(final Context context, AttributeSet attrs) {
		super(context, attrs);

		panel = new PopupPanel(R.layout.popup);
		setOnPanListener(new OnPanAndZoomListener() {

			@Override
			public void onZoom() {
			}

			@Override
			public void onPan() {
				panel.hide();
			}
		});

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

		final ResourceProxy mResourceProxy = new ResourceProxyImpl(getContext());
		final Resources resources = getResources();
		mMyLocationOverlay = new ItemizedOverlayWithFocus<ExtendedOverlayItem>(
				items,
				resources.getDrawable(R.drawable.station_pin),
				resources.getDrawable(R.drawable.station_marker),
				resources.getDrawable(R.drawable.station_pin_star),
				new StationInfoWindow(R.layout.maps_bubble, this),
				resources.getDimensionPixelSize(R.dimen.overlay_font_size),
				NOT_SET,
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
		mMyLocationOverlay.setFocusItemsOnTap(true);

		getOverlays().add(mMyLocationOverlay);
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
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
		}

		View getView() {
			return (popup);
		}

		void show(boolean alignTop) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( //
					RelativeLayout.LayoutParams.WRAP_CONTENT, //
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			final int intrinsicHeight = getResources().getDrawable(R.drawable.station_marker).getIntrinsicHeight();
			final float scaledDensity = getResources().getDisplayMetrics().density;
			System.out.println(-(getResources().getDrawable(R.drawable.station_marker).getIntrinsicHeight() * 2));
			lp.setMargins(0,
					getHeight() / 2 - (int)(intrinsicHeight * scaledDensity) - 25,
//					(int)((getResources().getDrawable(R.drawable.station_marker).getIntrinsicHeight() * 2) * scaledDensity),
				0, -10);
			ToastUtils.show(activity, "" + intrinsicHeight);
			
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

	// public Map<String, StationDetails> getMapOverlaysByStationId() {
	// return mapOverlaysByStationId;
	// }

}

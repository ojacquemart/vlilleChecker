package com.vlille.checker.ui.osm;

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

import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.HomeActivity;
import com.vlille.checker.ui.async.AbstractStationsAsyncTask;
import com.vlille.checker.ui.delegate.StationUpdateDelegate;
import com.vlille.checker.ui.osm.location.LocationManagerWrapper;
import com.vlille.checker.ui.osm.overlay.CircleLocationOverlay;
import com.vlille.checker.ui.osm.overlay.ItemizedOverlayWithFocus;
import com.vlille.checker.ui.osm.overlay.MaskableOverlayItem;
import com.vlille.checker.ui.osm.overlay.OverlayZoomUtils;
import com.vlille.checker.ui.osm.overlay.ResourceProxyImpl;
import com.vlille.checker.ui.osm.overlay.window.BubbleInfoWindow;
import com.vlille.checker.utils.ContextHelper;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="http://stackoverflow.com/questions/4729255/how-to-implemennt-onzoomlistener-on-mapview">Implement onZoomListener on MapView</a>
 */
public class MapView extends org.osmdroid.views.MapView implements LocationListener {

    /**
     * The default zoom level.
     */
    public static final int DEFAULT_ZOOM_LEVEL = 16;

    /**
     * The "Gare Lille Flandres" station.
     */
    public static final GeoPoint DEFAULT_CENTER_GEO_POINT = new GeoPoint(50636000, 3069680);

    private static final String TAG = MapView.class.getSimpleName();

    /**
     * Custom transport map
     */
    public static final int MAX_ZOOM_LEVEL = 18;
    public static final int MIN_ZOOM_LEVEL = 0;
    public static final int TILE_SIZE_PIXELS = 256;
    private static final XYTileSource PUBLIC_TRANSPORT = new XYTileSource("TransportMap",
            MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL,
            TILE_SIZE_PIXELS,
            ".png",
            new String[]{
                    "http://a.tile2.opencyclemap.org/transport/",
                    "http://b.tile2.opencyclemap.org/transport/",
                    "http://c.tile2.opencyclemap.org/transport/"
            });

    /**
     * The index for the location circle overlay.
     */
    private static final int OVERLAYS_LOCATION_CIRCLE_INDEX = 0;

    /**
     * The index for the stations overlays.
     */
    private static final int OVERLAYS_STATIONS_INDEX = 1;

    private MapState state;
    private List<Station> stations;

    private HomeActivity homeActivity;
    private StationUpdateDelegate stationUpdateDelegate;

    private boolean locationOn;
    private CircleLocationOverlay circleOverlay;
    private ItemizedOverlayWithFocus<MaskableOverlayItem> itemizedOverlay;

    public MapView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "MapView");
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
        updateStations();
        invalidate();
    }

    private void initConfiguration() {
        Log.d(TAG, "initConfiguration");

        setTileSource(PUBLIC_TRANSPORT);
        setBuiltInZoomControls(true);
        setMultiTouchControls(true);
        setTilesScaledToDpi(true);
    }

    private void initCenter() {
        Log.d(TAG, "initCenter");
        GeoPoint center = new GeoPoint(state.currentCenter);
        setCenter(center);
    }

    public void setCenter(GeoPoint center) {
        IMapController mMapController = getController();
        mMapController.setZoom(state.zoomLevel);
        mMapController.setCenter(center);
    }

    //=========
    // Location
    //=========

    public void updateLocationCircle() {
        this.locationOn = !locationOn;
        Log.d(TAG, "Location on: " + locationOn);
        if (locationOn) {
            requestLocationUpdates();
            drawLocationCircle();
        } else {
            reinitDefaultCenter();
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

    private void reinitDefaultCenter() {
        getController().setCenter(state.currentCenter);
        getLocationManager().removeUpdates(this);
        circleOverlay.setGeoPosition(null);
    }

    private LocationManager getLocationManager() {
        return (LocationManager) homeActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    private void drawLocationCircle() {
        Log.d(TAG, "drawLocationCircle");
        if (locationOn) {
            if (LocationManagerWrapper.with(getContext()).hasCurrentLocation()) {
                Location currentLocation = LocationManagerWrapper.with(getContext()).getCurrentLocation();
                Log.d(TAG, "Current location [lat=" + currentLocation.getLatitude() + ",long=" + currentLocation.getLongitude() + "]");
                final GeoPoint geoPoint = new GeoPoint(currentLocation);
                getController().setCenter(geoPoint);
                circleOverlay.setGeoPosition(geoPoint);
                updateStations();
            } else {
                homeActivity.showSnackBarMessage(R.string.error_no_location_found);
            }
        }
    }

    private void initCircleOverlay() {
        circleOverlay = new CircleLocationOverlay(getContext());
        getOverlays().add(OVERLAYS_LOCATION_CIRCLE_INDEX, circleOverlay);
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

    private static final ItemizedIconOverlay.OnItemGestureListener<MaskableOverlayItem> DEFAULT_ONGESTURE_LISTENER = new ItemizedIconOverlay.OnItemGestureListener<MaskableOverlayItem>() {
        @Override
        public boolean onItemSingleTapUp(final int index, final MaskableOverlayItem item) {
            return false;
        }

        @Override
        public boolean onItemLongPress(final int index, final MaskableOverlayItem item) {
            return false;
        }
    };
    private final ResourceProxy mResourceProxy = new ResourceProxyImpl(getContext());

    private List<MaskableOverlayItem> maskableOverlayItems = null;

    private void initIconizedOverlay() {
        final List<MaskableOverlayItem> items = initOverlays();

        final Resources resources = getResources();
        if (itemizedOverlay == null) {
            BubbleInfoWindow bubbleInfoWindow = new BubbleInfoWindow(this, homeActivity, stationUpdateDelegate);
            itemizedOverlay = new ItemizedOverlayWithFocus<MaskableOverlayItem>(
                    items,
                    resources,
                    bubbleInfoWindow,
                    DEFAULT_ONGESTURE_LISTENER,
                    mResourceProxy
            );
        }
        itemizedOverlay.setFocusItemsOnTap(true);

        getOverlays().add(OVERLAYS_STATIONS_INDEX, itemizedOverlay);
    }

    private List<MaskableOverlayItem> initOverlays() {
        if (maskableOverlayItems != null) {
            return maskableOverlayItems;
        }

        maskableOverlayItems = new ArrayList<MaskableOverlayItem>();
        if (stations != null) {
            for (Station eachStation : stations) {
                final MaskableOverlayItem extendedOverlayItem = new MaskableOverlayItem(
                        eachStation.getName(), eachStation.getName(),
                        eachStation.getGeoPoint());
                extendedOverlayItem.setRelatedObject(eachStation);

                maskableOverlayItems.add(extendedOverlayItem);
            }
        }

        return maskableOverlayItems;
    }

    private void setOnPanZoomListener() {
        setOnPanListener(new OnPanAndZoomListener() {

            @Override
            public void onZoom() {
                Log.d(TAG, String.format("onZoom (actualZoom=%d/maxZoom=%d)", getZoomLevel(), getMaxZoomLevel()));
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
        itemizedOverlay.hideBubble();

        Log.i(TAG, "update Stations");
        if (itemizedOverlay.getBubble() != null) {
            itemizedOverlay.getBubble().setZoomLevel(getZoomLevel());
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

        if (ContextHelper.isNetworkAvailable(getContext())) {
            if (stations.isEmpty()) {
                itemUpdater.whenNoneStationToDraw();

                // Some stations may have seen their visibility attribute changed.
                invalidate();
            } else {
                if (OverlayZoomUtils.isDetailledZoomLevel(getZoomLevel())) {
                    Log.d(TAG, String.format("%d stations to update!", stations.size()));

                    AsyncMapStationRetriever asyncTask = new AsyncMapStationRetriever(stationUpdateDelegate);
                    asyncTask.execute(stations);
                }
            }
        }
    }

    private ItemActionUpdater getItemUpdater() {
        return getDefaultItemUpdater();
    }

    /**
     * The classic update just make the station visible if the
     * station is displayed in current screen and if the zoom
     * level allows it.
     *
     * @return the classic updater.
     * @see OverlayZoomUtils#isDetailledZoomLevel(int)
     * @see org.osmdroid.views.MapView#getBoundingBox()
     */
    private ItemActionUpdater getDefaultItemUpdater() {
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

            public void whenNoneStationToDraw() {
            }

        };
    }

    class AsyncMapStationRetriever extends AbstractStationsAsyncTask {

        AsyncMapStationRetriever(StationUpdateDelegate delegate) {
            super(homeActivity, delegate);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            homeActivity.setRefreshActionButtonState(true);
        }

        @Override
        protected void onPostExecute(List<Station> result) {
            super.onPostExecute(result);
            homeActivity.setRefreshActionButtonState(false);
            invalidate();
        }
    }

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
        if (panAndZoomListener != null && getZoomLevel() != oldZoomLevel) {
            panAndZoomListener.onZoom();
            oldZoomLevel = getZoomLevel();
        }
    }

    public void setOnPanListener(OnPanAndZoomListener listener) {
        panAndZoomListener = listener;
    }

    public boolean isLocationOn() {
        return locationOn;
    }

    public void setHomeActivity(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    public void setStationUpdateDelegate(StationUpdateDelegate stationUpdateDelegate) {
        this.stationUpdateDelegate = stationUpdateDelegate;
    }

}

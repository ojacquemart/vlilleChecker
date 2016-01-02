package com.vlille.checker.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vlille.checker.R;
import com.vlille.checker.db.StationEntityManager;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.HomeActivity;
import com.vlille.checker.ui.delegate.StationUpdateDelegate;
import com.vlille.checker.ui.osm.MapState;
import com.vlille.checker.ui.osm.MapView;
import com.vlille.checker.utils.ToastUtils;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.fragment.support.v4.Fragment;
import org.osmdroid.util.GeoPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment to localize and bookmark stations from a map, using OpenStreetMap.
 */
public class MapFragment extends Fragment implements StationUpdateDelegate {

    private static final String TAG = MapFragment.class.getSimpleName();

    private static final Map<Integer, PermissionConfig> PERMISSIONS_CONFIG = new HashMap<>();
    static {
        PERMISSIONS_CONFIG.put(PermissionConfig.LOCATION, new PermissionConfig(PermissionConfig.LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                R.string.permission_location_explanation,
                R.string.permission_location_not_granted));
        PERMISSIONS_CONFIG.put(PermissionConfig.STORAGE, new PermissionConfig(PermissionConfig.STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                R.string.permission_storage_explanation,
                R.string.permission_storage_not_granted));
    }

    @InjectDependency
    private StationEntityManager stationEntityManager;
    private MapState state = new MapState();

    private MapView mapView;

    private List<Station> stations;

    static class PermissionConfig {
        public static int LOCATION = 1;
        public static int STORAGE = 2;

        int requestCode;
        String permission;
        int explanation;
        int notGrantedText;

        PermissionConfig(int requestCode, String permission, int explanation, int notGrantedText) {
            this.requestCode = requestCode;
            this.permission = permission;
            this.explanation = explanation;
            this.notGrantedText = notGrantedText;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(TAG, "onCreate");

        checkPermissions();

        if (!this.state.isInitialized()) {
            this.state.save(MapView.DEFAULT_CENTER_GEO_POINT, MapView.DEFAULT_ZOOM_LEVEL);
        }
    }

    private void checkPermissions() {
        Log.d(TAG, "checkPermissions");
        for (PermissionConfig permissionConfig : PERMISSIONS_CONFIG.values()) {
            checkPermission(permissionConfig);
        }
    }

    private void checkPermission(PermissionConfig permissionConfig) {
        Log.v(TAG, "Check for permission " + permissionConfig.permission);

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), permissionConfig.permission);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionConfig.permission)) {
                ToastUtils.show(getActivity(), permissionConfig.explanation);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(), new String[]{permissionConfig.permission}, permissionConfig.requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        PermissionConfig permissionConfig = PERMISSIONS_CONFIG.get(requestCode);
        if (permissionConfig != null) {
            handleOnRequestPermissionsResult(permissionConfig, permissions, grantResults);
        }
    }

    private void handleOnRequestPermissionsResult(PermissionConfig permissionConfig, String[] permissions, int[] grantResults) {
        Log.v(TAG, "Handle permission result for " + permissionConfig.permission);
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (!granted) {
            ToastUtils.show(getActivity(), permissionConfig.notGrantedText);
        }
    }

    public void setCenter(GeoPoint center) {
        Log.d(TAG, "setCenter " + center);
        this.state.currentCenter = center;
        this.state.zoomLevel = MapView.DEFAULT_ZOOM_LEVEL;
    }

    @Override
    public View onCreateView(Bundle savedInstanceState,
                             LayoutInflater inflater, ViewGroup container) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.maps, container, false);
        mapView = (MapView) view.findViewById(R.id.mapview);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.stations = stationEntityManager.findAll();

        mapView.setMapInfos(state, stations);
        mapView.setHomeActivity((HomeActivity) getActivity());
        mapView.setStationUpdateDelegate(this);
        mapView.init();

        addLocationEnablerClickListener(getView());
    }

    @Override
    public void update(Station station) {
        stationEntityManager.update(station);
    }

    private void addLocationEnablerClickListener(final View view) {
        final FloatingActionButton locationEnabler = (FloatingActionButton) view.findViewById(R.id.maps_location_enable);
        locationEnabler.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mapView.updateLocationCircle();
                locationEnabler.setImageResource(mapView.isLocationOn() ? R.drawable.ic_location_on : R.drawable.ic_location_off);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mapView.updateStations();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        GeoPoint mapCenter = (GeoPoint) mapView.getMapCenter();
        if (mapView.isLocationOn()) {
            mapCenter = null;
        }
        state.save(mapCenter, mapView.getZoomLevel());
    }

}

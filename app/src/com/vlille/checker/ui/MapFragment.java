package com.vlille.checker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.vlille.checker.R;
import com.vlille.checker.db.StationEntityManager;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.osm.MapState;
import com.vlille.checker.ui.osm.MapView;

import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.fragment.sherlock.Fragment;
import org.osmdroid.util.GeoPoint;

import java.util.List;
 
/**
 * A fragment to localize and bookmark stations from a map, using OpenStreetMap.
 */
public class MapFragment extends Fragment implements StationUpdateDelegate {

	private static final String TAG = MapFragment.class.getSimpleName();

    @InjectDependency
    private StationEntityManager stationEntityManager;

	private MapState state = new MapState();
	private MapView mapView;
	private List<Station> stations;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Log.d(TAG, "onCreate");

        if (!this.state.isInitialized()) {
            this.state.save(MapView.DEFAULT_CENTER_GEO_POINT, MapView.DEFAULT_ZOOM_LEVEL);
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
        mapView.setSherlockActivity(getSherlockActivity());
        mapView.setStationUpdateDelegate(this);
        mapView.init();

        addLocationEnablerClickListener(getView());
    }

    @Override
    public void update(Station station) {
        stationEntityManager.update(station);
    }

    private void addLocationEnablerClickListener(final View view) {
        final ImageButton locationEnabler = (ImageButton) view.findViewById(R.id.maps_location_enable);
        locationEnabler.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mapView.updateLocationCircle();
                if (mapView.isLocationOn()) {
                    locationEnabler.setImageResource(R.drawable.ic_location_on);
                } else {
                    locationEnabler.setImageResource(R.drawable.ic_location_off);
                }
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

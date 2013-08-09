package com.vlille.checker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.osm.MapState;
import com.vlille.checker.ui.osm.VlilleMapView;

import org.osmdroid.util.GeoPoint;

import java.util.List;
 
/**
 * A fragment to localize and bookmark stations from a map, using OpenStreetMap.
 */
public class MapFragment extends SherlockFragment {

	private static final String TAG = MapFragment.class.getSimpleName();

	private MapState state = new MapState();
	private VlilleMapView mapView;
	private List<Station> stations;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Log.d(TAG, "onCreate");
		
		SetStationsInfos setStationsInfos = VlilleChecker.getDbAdapter().findSetStationsInfos();
        if (!this.state.isInitialized()) {
            this.state.save(VlilleMapView.DEFAULT_CENTER_GEO_POINT, VlilleMapView.DEFAULT_ZOOM_LEVEL);
        }
		this.stations = setStationsInfos.getStations();

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreateView");

		final View view = inflater.inflate(R.layout.maps, container, false);
		mapView = (VlilleMapView) view.findViewById(R.id.mapview);
		mapView.setMapInfos(state, stations);
		mapView.setSherlockActivity(getSherlockActivity());
		mapView.init();
		
		addLocationEnablerClickListener(view);
		
		return view;
	}

    public void setCenter(GeoPoint center) {
        Log.d(TAG, "setCenter " + center);
        this.state.currentCenter = center;
        this.state.zoomLevel = VlilleMapView.DEFAULT_ZOOM_LEVEL;
    }

	private void addLocationEnablerClickListener(final View view) {
		final ImageButton locationEnabler = (ImageButton) view.findViewById(R.id.maps_location_enable);
		locationEnabler.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mapView.updateLocationCircle();
				if (mapView.isLocationOn()) {
					locationEnabler.setImageResource(R.drawable.location_on);
				} else {
					locationEnabler.setImageResource(R.drawable.location_off);
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

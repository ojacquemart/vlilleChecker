package com.vlille.checker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Window;
import com.vlille.checker.R;
import com.vlille.checker.ui.osm.VlilleMapView;
import com.vlille.checker.utils.ToastUtils;
 
/**
 * Select stations from maps.
 * It allows to select your station browsing the stations map.
 */
public class MapFragment extends SherlockFragment {

	private final String TAG = getClass().getSimpleName();
	
	private VlilleMapView mapView;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Log.d(TAG, "onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreateView");
		
		final View view = inflater.inflate(R.layout.maps, container, false);
		mapView = (VlilleMapView) view.findViewById(R.id.mapview);
		mapView.setSherlockActivity(getSherlockActivity());
		mapView.invalidate();
		
		addLocationEnablerClickListener(view);
		
		return view;
	}

	private void addLocationEnablerClickListener(final View view) {
		final ImageButton locationEnabler = (ImageButton) view.findViewById(R.id.maps_location_enable);
		locationEnabler.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ToastUtils.show(getActivity(), "Location on!");
				mapView.updateLocationCircle();
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		mapView.updateStations();
	}

}

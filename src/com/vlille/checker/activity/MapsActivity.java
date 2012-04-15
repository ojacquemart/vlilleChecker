package com.vlille.checker.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.maps.MapActivity;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.vlille.checker.R;
import com.vlille.checker.maps.CustomMapView;
import com.vlille.checker.maps.OnPanAndZoomListener;
import com.vlille.checker.utils.ApplicationContextHelper;
import com.vlille.checker.utils.Toaster;
 
public class MapsActivity extends MapActivity {

	private static final String LOG_TAG_MAPS_ACTIVITY = "MapsActivity";
	private CustomMapView mapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		
		initActionBar();
		
		mapView = (CustomMapView) findViewById(R.id.mapview);
		
		try {
			mapView.initOverlays(ApplicationContextHelper.parseAllStations(this));
		} catch (RuntimeException e) {
			Log.e(LOG_TAG_MAPS_ACTIVITY, "an exception occured", e);
			Toaster.withContext(getApplicationContext()).noConnection();
		}
		
		mapView.setOnPanListener(new OnPanAndZoomListener() {
			
			@Override
			public void onZoom() {
				mapView.updateOverlays();
			}
			
			@Override
			public void onPan() {
				mapView.updateOverlays();
			}
		});
	}
	
	private void initActionBar() {
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.addAction(new RefreshAction());
	}

	@Override
	public void onResume() {
		super.onResume();
		
		mapView.checkDelay();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private class RefreshAction extends AbstractAction {

        public RefreshAction() {
            super(R.drawable.ic_menu_refresh);
        }

        @Override
        public void performAction(View view) {
        	mapView.updateOverlays();
        }

    }	
	
}

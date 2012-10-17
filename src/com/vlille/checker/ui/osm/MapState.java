package com.vlille.checker.ui.osm;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

/**
 * Class which saves the current state of the map.
 * 
 * @see VlilleMapView#getZoomLevel()
 * @see VlilleMapView#getMapCenter()
 */
public class MapState {

	public GeoPoint currentCenter;
	public int zoomLevel;
	
	public void set(IGeoPoint currentCenter, int zoomLevel) {
		this.currentCenter = (GeoPoint) currentCenter;
		this.zoomLevel = zoomLevel;
	}
}

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

    /**
     * Checks if state has been initialized.
     */
    public boolean isInitialized() {
        return currentCenter != null && zoomLevel > 0;
    }
	
	/**
	 * Save current map state.
	 * 
	 * @param currentCenter if value is not null, set the value. Can be null when map location is on.
	 * @param zoomLevel the current zoomLevel.
	 */
	public void save(IGeoPoint currentCenter, int zoomLevel) {
		if (currentCenter != null) {
			this.currentCenter = (GeoPoint) currentCenter;
		}
		this.zoomLevel = zoomLevel;
	}
}

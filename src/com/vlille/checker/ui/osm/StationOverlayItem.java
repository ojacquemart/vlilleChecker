package com.vlille.checker.ui.osm;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import com.vlille.checker.model.Station;

public class StationOverlayItem extends OverlayItem {

	private boolean starred;

	public StationOverlayItem(Station station) {
		this(station.getName(), station.isStarred(), station.getPoint());
	}
	
	public StationOverlayItem(String aTitle, boolean starred, GeoPoint aGeoPoint) {
		super(aTitle, aTitle, aGeoPoint);
	}

	public boolean isStarred() {
		return starred;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
	}

}

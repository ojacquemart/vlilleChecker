package com.vlille.checker.maps.overlay;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

public class StationOverlayItem extends OverlayItem {

	private boolean starred;

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

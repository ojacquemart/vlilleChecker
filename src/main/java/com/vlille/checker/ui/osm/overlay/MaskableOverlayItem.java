package com.vlille.checker.ui.osm.overlay;

import org.osmdroid.util.GeoPoint;

import android.content.Context;

public class MaskableOverlayItem extends ExtendedOverlayItem {

	private boolean visible;

	public MaskableOverlayItem(String aTitle, String aDescription, GeoPoint aGeoPoint, Context context) {
		super(aTitle, aDescription, aGeoPoint, context);
	}

	public boolean isVisible() {
		return visible;
	}
	
	public boolean isHidden() {
		return !visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}

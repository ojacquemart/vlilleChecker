package com.vlille.checker.model;

import android.content.ContentValues;

import com.vlille.checker.db.GetContentValues;
import com.vlille.checker.db.MapInfosTableFields;

public class StationsMapsInfos implements GetContentValues {

	/**
	 * Default latitude.
	 */
	private int latitude1e6;
	/**
	 * Default longitude.
	 */
	private int longitude1e6;
	/**
	 * Default zoom level.
	 */
	private int zoom;

	public int getLatitude1e6() {
		return latitude1e6;
	}

	public void setLatitude1e6(int latitude) {
		this.latitude1e6 = latitude;
	}

	public int getLongitude1e6() {
		return longitude1e6;
	}

	public void setLongitude1e6(int longitude) {
		this.longitude1e6 = longitude;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	@Override
	public ContentValues getInsertableContentValues() {
		ContentValues values = new ContentValues();
		values.put(MapInfosTableFields.LATITUDE, latitude1e6);
		values.put(MapInfosTableFields.LONGITUTDE, longitude1e6);
		
		return values;
	}

	@Override
	public ContentValues getUpdatableContentValues() {
		return null;
	}

}

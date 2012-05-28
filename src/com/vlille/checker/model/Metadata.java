package com.vlille.checker.model;

import android.content.ContentValues;

import com.vlille.checker.db.GetContentValues;
import com.vlille.checker.db.metadata.MetadataTableFields;

public class Metadata implements GetContentValues {

	/**
	 * Last update when was checked the vlille stations.
	 */
	private long lastUpdate;
	
	/**
	 * Default latitude.
	 */
	private int latitude1e6;
	
	/**
	 * Default longitude.
	 */
	private int longitude1e6;

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

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

	@Override
	public ContentValues getInsertableContentValues() {
		ContentValues values = new ContentValues();
		values.put(MetadataTableFields.lastUpdate.toString(), System.currentTimeMillis());
		values.put(MetadataTableFields.latitudeE6.toString(), latitude1e6);
		values.put(MetadataTableFields.longitudeE6.toString(), longitude1e6);
		
		return values;
	}

	@Override
	public ContentValues getUpdatableContentValues() {
		return null;
	}

}

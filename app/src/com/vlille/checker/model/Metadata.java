package com.vlille.checker.model;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;
import org.osmdroid.util.GeoPoint;

import com.vlille.checker.db.DB;

@Table(name = DB.Table.METADATA)
public class Metadata extends Entity {

    public static final String LAST_UPDATE = "lastUpdate";
    public static final String LATITUDE_E6 = "latitudeE6";
    public static final String LONGITUDE_E6 = "longitudeE6";

	/**
	 * Last update when was checked the vlille stations.
	 */
    @Column(name = LAST_UPDATE)
	public long lastUpdate;
	
	/**
	 * Default latitude.
	 */
    @Column(name = LATITUDE_E6)
    public int latitude1e6;
	
	/**
	 * Default longitude.
	 */
    @Column(name = LONGITUDE_E6)
    public int longitude1e6;
	
	public GeoPoint getGeoPoint() {
		return new GeoPoint(latitude1e6, longitude1e6);
	}

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

}

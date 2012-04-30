package com.vlille.checker.model;


public class StationsMapsInfos {

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

}

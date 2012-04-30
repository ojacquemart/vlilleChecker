package com.vlille.checker.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.vlille.checker.stations.Constants;

/**
 * Represents the details of a single vlille station.
 */
public class Station implements Comparable<Station>, Parcelable {

	private static final int HASHCODE_VALUE = 11;
	private static final int HASHCODE_MULTIPLIER = 37;
	
	/**
	 * From stations list.
	 */
	private String id;
	private String name;
	private double latitude;
	private double longitude;
	private int latitudeE6;
	private int longituteE6;

	/**
	 * From detail stations informations.
	 */
	private String adress;
	private String status;
	private String bikes;
	private String attachs;
	private String paiement;
	private String lastUpdated;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLatitudeE6() {
		return latitudeE6;
	}

	public void setLatitudeE6(int latitudeE6) {
		this.latitudeE6 = latitudeE6;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getLongituteE6() {
		return longituteE6;
	}
	

	public void setLongituteE6(int longituteE6) {
		this.longituteE6 = longituteE6;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getStatus() {
		return status;
	}
	
	public boolean isOutOfService() {
		return !status.equals(Constants.FLAG_OUT_OF_SERVICE);
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getBikes() {
		return Integer.valueOf(bikes);
	}

	public void setBikes(String bikes) {
		this.bikes = bikes;
	}

	public Integer getAttachs() {
		return Integer.valueOf(attachs);
	}

	public void setAttachs(String attachs) {
		this.attachs = attachs;
	}

	public String getPaiement() {
		return paiement;
	}
	
	public boolean isCb() {
		return paiement.equals(Constants.FLAG_ALLOWS_CB);
	}

	public void setPaiement(String paiement) {
		this.paiement = paiement;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Station)) {
			return false;
		}
		if (o == this) {
			return true;
		}
		
		Station other = (Station) o;
		if (id.equals(other.getId())) {
			return true;
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return HASHCODE_VALUE * HASHCODE_MULTIPLIER * id.hashCode();
	}

	@Override
	public int compareTo(Station another) {
		return name.compareTo(another.getName());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	}

}

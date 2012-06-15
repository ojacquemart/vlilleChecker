package com.vlille.checker.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import android.content.ContentValues;

import com.vlille.checker.db.GetContentValues;
import com.vlille.checker.db.station.StationTableFields;
import com.vlille.checker.maps.PositionTransformer;
import com.vlille.checker.utils.Constants;

/**
 * Represents the details of a single vlille station.
 */
public class Station implements Serializable, GetContentValues {
	
	private static final long serialVersionUID = 1L;
	
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
	private String bikes;
	private String attachs;
	private boolean cbPaiement;
	private boolean outOfService;
	private long lastUpdate;
	private boolean starred;
	
	/**
	 * To sort. TODO: implement the solution.
	 */
	private Integer ordinal;
	
	public Station() {
	}
	
	public Station(String id) {
		this.id = id;
	}
	
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
	

	public void setLongitudeE6(int longituteE6) {
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

	public boolean isOutOfService() {
		return outOfService;
	}

	public void setOufOfService(boolean outOfService) {
		this.outOfService = outOfService;
	}
	
	public String getStringBikes() {
		return bikes; 
	}

	public Integer getBikes() {
		return NumberUtils.toInt(bikes, NumberUtils.INTEGER_ZERO);
	}

	public void setBikes(String bikes) {
		this.bikes = bikes;
	}
	
	public String getStringAttachs() {
		return attachs;
	}

	public Integer getAttachs() {
		return NumberUtils.toInt(attachs, NumberUtils.INTEGER_ZERO);
	}

	public void setAttachs(String attachs) {
		this.attachs = attachs;
	}

	public boolean isCbPaiement() {
		return cbPaiement;
	}

	public void setCbPaiement(boolean cbPaiement) {
		this.cbPaiement = cbPaiement;
	}
	
	public boolean isUpToDate() {
		final long now = System.currentTimeMillis();
		final long pastUpdate = lastUpdate - (now - Constants.ONE_MINUTE_IN_MILLSECONDS);
		boolean upToDate = pastUpdate + Constants.ONE_MINUTE_IN_MILLSECONDS > 0;
		if (!upToDate) {
			lastUpdate = now;
		}
		
		return upToDate;
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public boolean isStarred() {
		return starred;
	}


	public void setStarred(boolean starred) {
		this.starred = starred;
	}


	public Integer getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}

	/**
	 * Put all data from the listing stations, all attributes will be set to null.
	 */
	@Override
	public ContentValues getInsertableContentValues() {
		ContentValues values = new ContentValues();
		values.put(StationTableFields._id.toString(), id);
		values.put(StationTableFields.suggest_text_1.toString(), name);
		values.put(StationTableFields.adress.toString(), adress);
		values.put(StationTableFields.starred.toString(), 0);
		values.put(StationTableFields.latitude.toString(), latitude);
		values.put(StationTableFields.longitude.toString(), longitude);
		values.put(StationTableFields.latitudeE6.toString(), PositionTransformer.toE6(latitude));
		values.put(StationTableFields.longitudeE6.toString(), PositionTransformer.toE6(longitude));
		
		return values;
	}

	/**
	 * Put all updatable data from the detailled station.
	 */
	@Override
	public ContentValues getUpdatableContentValues() {
		ContentValues values = new ContentValues();
		values.put(StationTableFields.adress.toString(), adress);
		values.put(StationTableFields.bikes.toString(), bikes);
		values.put(StationTableFields.attachs.toString(), attachs);
		values.put(StationTableFields.cbPaiement.toString(), cbPaiement);
		values.put(StationTableFields.outOfService.toString(), outOfService);
		values.put(StationTableFields.lastUpdate.toString(), lastUpdate);
		
		return values;
	}

	public void copyParsedInfos(Station parsedStation) {
		this.adress = parsedStation.getAdress();
		this.bikes = parsedStation.getStringBikes();
		this.attachs = parsedStation.getStringAttachs();
		this.cbPaiement = parsedStation.isCbPaiement();
		this.outOfService = parsedStation.isOutOfService();
		this.lastUpdate = parsedStation.getLastUpdate();
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
		return new EqualsBuilder().append(name, other.getName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).toHashCode();
	}

}

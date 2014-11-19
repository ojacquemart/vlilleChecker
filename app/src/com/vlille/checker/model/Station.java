package com.vlille.checker.model;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;
import org.osmdroid.util.GeoPoint;

import android.text.TextUtils;

import com.vlille.checker.db.DB;
import com.vlille.checker.utils.NumberUtils;

/**
 * Represents the details of a single vlille station.
 */
@Table(name = DB.Table.STATION)
public class Station extends Entity {

    public static final String ID = "_id";
    public static final String NAME = "suggest_text_1";
    public static final String LATITUDE = "latitude";
    public static final String LATITUDE_E6 = "latitudeE6";
    public static final String LONGITUDE = "longitude";
    public static final String LONGITUDE_E6 = "longitudeE6";
    public static final String ADDRESS = "adress";
    public static final String BIKES = "bikes";
    public static final String ATTACHS = "attachs";
    public static final String CC_PAYMENT = "cbPaiement";
    public static final String OUT_OF_SERVICE = "outOfService";
    public static final String LAST_UPDATE = "lastUpdate";
    public static final String STARRED = "starred";
    public static final String ORDINAL = "ordinal";

    public static final String EMPTY_VALUE = "...";

    /**
     * Nullable columns:
     * - address
     * - attachs
     * - bikes
     * - ordinal
     */

    @Column(name = NAME)
    public String name;

    @Column(name = LATITUDE)
    public double latitude;

    @Column(name = LATITUDE_E6)
    public int latitudeE6;

    @Column(name = LONGITUDE)
    public double longitude;

    @Column(name = LONGITUDE_E6)
    public int longitudeE6;

    @Column(name = ADDRESS, nullable = true)
    public String adress;

    @Column(name = BIKES, nullable = true)
    public String bikes;

    @Column(name = ATTACHS, nullable = true)
    public String attachs;

    @Column(name = CC_PAYMENT)
    public boolean cbPaiement;

    @Column(name = OUT_OF_SERVICE)
    public boolean outOfService;

    @Column(name = LAST_UPDATE)
    public long lastUpdate;

    @Column(name = STARRED)
    public boolean starred;

    @Column(name = ORDINAL, nullable = true)
    public int ordinal;

    public Station() {
    }

    public GeoPoint getGeoPoint() {
        return new GeoPoint(latitudeE6, longitudeE6);
    }

    // Getters & setters.

    public long getId() {
        return id;
    }

    public String getName() {
        return id + " - " + name;
    }

    public String getName(boolean idVisible) {
        if (idVisible) {
            return getName();
        }

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

    public String getLatitudeAsString() {
        return String.valueOf(latitude);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLongitudeAsString() {
        return String.valueOf(longitude);
    }

    public int getLongitudeE6() {
        return longitudeE6;
    }

    public void setLongitudeE6(int longitudeE6) {
        this.longitudeE6 = longitudeE6;
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

    public String getAdressToUpperCase() {
        if (adress == null) {
            return "";
        }

        return adress.toUpperCase();
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

    public String getBikesAsString() {
        return getStringValue(bikes);
    }

    public Integer getBikes() {
        return NumberUtils.toInt(bikes, NumberUtils.INTEGER_ZERO);
    }

    public void setBikes(String bikes) {
        this.bikes = bikes;
    }

    public String getAttachsAsString() {
        return getStringValue(attachs);
    }

    private String getStringValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return EMPTY_VALUE;
        }

        return value;
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

        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}

package com.vlille.checker.model;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;

import com.vlille.checker.R;
import com.vlille.checker.db.DB;
import com.vlille.checker.utils.NumberUtils;
import com.vlille.checker.utils.TextPlural;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;
import org.osmdroid.util.GeoPoint;

import java.util.Arrays;
import java.util.List;

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
    public static final String APPWIDGET_ID = "appWidgetId";
    public static final int APPWIDGET_ID_EMPTY_VALUE = -1;

    public static final String EMPTY_VALUE = "...";

    public static final long LILLE_FLANDRES_ID = 24L;
    public static final List<Long> EXPRESS_IDS = Arrays.asList(LILLE_FLANDRES_ID);

    public static final int ONE_MINUTE_IN_SECONDS = 60;

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

    @Column(name = APPWIDGET_ID, nullable = true)
    public int appWidgetId = -1;

    private boolean fetchInError;

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

    public int getOutOfServiceVisibility() {
        return outOfService ? View.VISIBLE : View.GONE;
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

    public boolean isExpress() {
        return EXPRESS_IDS.contains(id);
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public String getLastUpdateAsString(Resources resources) {
        return getLastUpdateAsStringFromResources(resources, LastUpdateDisplayType.NORMAL);
    }

    public String getShortLastUpdateAsString(Resources resources) {
        return getLastUpdateAsStringFromResources(resources, LastUpdateDisplayType.SHORT);
    }

    private String getLastUpdateAsStringFromResources(Resources resources, LastUpdateDisplayType lastUpdateDisplayType) {
        if (isLastUpdateTimeExceedTwoMinutes()) {
            if (LastUpdateDisplayType.SHORT == lastUpdateDisplayType) {
                return resources.getString(LastUpdateDisplayType.LONG_AGO_SHORT.getResourceId());
            }

            return resources.getString(LastUpdateDisplayType.LONG_AGO.getResourceId());
        }

        boolean isLastUpdateExceedOneMinute = lastUpdate > ONE_MINUTE_IN_SECONDS;
        int timeUnitResourceId = isLastUpdateExceedOneMinute
                ? R.string.timeunit_minute
                : R.string.timeunit_second;
        Long lastUpdateForTimeUnit = isLastUpdateExceedOneMinute
                ? Math.round(lastUpdate / ONE_MINUTE_IN_SECONDS)
                : lastUpdate;
        String timeUnit = TextPlural.toPlural(
                lastUpdateForTimeUnit,
                resources.getString(timeUnitResourceId));

        return resources.getString(lastUpdateDisplayType.getResourceId(), lastUpdateForTimeUnit, timeUnit);
    }

    public boolean isLastUpdateTimeExceedTwoMinutes() {
        return getLastUpdate() > ONE_MINUTE_IN_SECONDS * 2;
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

    public int getAppWidgetId() {
        return appWidgetId;
    }

    public void setAppWidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    public boolean isFetchInError() {
        return fetchInError;
    }

    public void setFetchInError() {
        this.fetchInError = true;
    }

    public void setFetchOk() {
        this.fetchInError = false;
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

        return id == other.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}

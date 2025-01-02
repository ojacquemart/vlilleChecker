package com.vlille.checker.dataset.retrofit.model;

import com.google.gson.annotations.SerializedName;
import com.vlille.checker.dataset.retrofit.VlilleService;
import com.vlille.checker.ui.osm.PositionTransformer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class Station {
    public static final String REGEX_NAME_NO_ID = "^(?:\\d+\\s*)(.*)";
    public static final String REGEX_NAME_NO_CB = "(.*)(?:\\s*\\(.*\\))$";
    public static final String EN_SERVICE = "EN SERVICE";
    public static final String AVEC_TPE = "AVEC TPE";
    public static final String LAST_UPDATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String EUROPE_PARIS_ZONE_ID = "Europe/Paris";

    @SerializedName("@id")
    public String id;
    @SerializedName("nom")
    public String name;
    @SerializedName("etat")
    public String status;
    @SerializedName(value = "nb_velos_dispo")
    public int bikes;
    @SerializedName(value = "nb_places_dispo")
    public int attachs;
    @SerializedName("adresse")
    public String address;
    @SerializedName("type")
    public String paymentType;
    @SerializedName("x")
    public double x;
    @SerializedName("y")
    public double y;
    @SerializedName("date_modification")
    public String lastUpdateAsString;

    public com.vlille.checker.model.Station toLegacy() {
        com.vlille.checker.model.Station legacy = new com.vlille.checker.model.Station();
        legacy.id = getId();

        legacy.name = getNameWithoutIndAndPaymentIndicator();
        legacy.adress = address;
        legacy.attachs = String.valueOf(attachs);
        legacy.bikes = String.valueOf(bikes);
        legacy.outOfService = !EN_SERVICE.equals(status);
        legacy.cbPaiement = AVEC_TPE.equals(paymentType);
        legacy.latitude = getLatitude();
        legacy.longitude = getLongitude();
        legacy.latitudeE6 = PositionTransformer.toE6(getLatitude());
        legacy.longitudeE6 = PositionTransformer.toE6(getLongitude());

        if (lastUpdateAsString != null) {
            legacy.lastUpdate = getLastUpdateInSeconds();
        }

        return legacy;
    }

    private Long getId() {
        return Long.valueOf(id.replace(VlilleService.COLLECTION + ".", ""));
    }

    private long getLastUpdateInSeconds() {
        Instant now = Instant.now();
        ZoneId parisZoneId = ZoneId.of(EUROPE_PARIS_ZONE_ID);
        ZonedDateTime parisDateTime = now.atZone(parisZoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LAST_UPDATE_PATTERN);
        LocalDateTime lastUpdate = LocalDateTime.parse(lastUpdateAsString, formatter);

        return ChronoUnit.SECONDS.between(lastUpdate, parisDateTime);
    }

    public String getNameWithoutIndAndPaymentIndicator() {
        // TODO: see to use only one regex...

        return name.replaceAll(REGEX_NAME_NO_ID, "$1").replaceAll(REGEX_NAME_NO_CB, "$1");
    }

    public double getLatitude() {
        return this.y;
    }

    public double getLongitude() {
        return this.x;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", bikes=" + bikes +
                ", attachs=" + attachs +
                ", address='" + address + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

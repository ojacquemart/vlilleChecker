package com.vlille.checker.dataset.retrofit.model;

import com.google.gson.annotations.SerializedName;
import com.vlille.checker.ui.osm.PositionTransformer;

import java.util.Arrays;

public class Station {
    public static final String REGEX_NAME_NO_ID = "^(?:\\d+\\s*)(.*)";
    public static final String REGEX_NAME_NO_CB = "(.*)(?:\\s*\\(.*\\))$";
    public static final String EN_SERVICE = "EN SERVICE";
    public static final String AVEC_TPE = "AVEC TPE";

    @SerializedName("libelle")
    public long id;
    @SerializedName("nom")
    public String name;
    @SerializedName("etat")
    public String status;
    @SerializedName("nbVelosDispo")
    public int bikes;
    @SerializedName("nbPlacesDispo")
    public int attachs;
    @SerializedName("adresse")
    public String address;
    @SerializedName("type")
    public String paymentType;
    @SerializedName("geo")
    public double[] coordinates;

    public com.vlille.checker.model.Station toLegacy() {
        com.vlille.checker.model.Station legacy = new com.vlille.checker.model.Station();
        legacy.id = id;

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

        return legacy;
    }

    public String getNameWithoutIndAndPaymentIndicator() {
        // TODO: see to use only one regex...

        return name.replaceAll(REGEX_NAME_NO_ID, "$1").replaceAll(REGEX_NAME_NO_CB, "$1");
    }

    public double getLatitude() {
        return this.coordinates[0];
    }

    public double getLongitude() {
        return this.coordinates[1];
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
                ", coordinates=" + Arrays.toString(coordinates) +
                '}';
    }
}

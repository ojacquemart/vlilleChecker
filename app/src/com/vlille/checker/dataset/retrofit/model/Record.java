package com.vlille.checker.dataset.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Record {
    @SerializedName("fields")
    public Station station;
    @SerializedName("record_timestamp")
    public Date lastUpdate;

    public com.vlille.checker.model.Station toLegacy() {
        com.vlille.checker.model.Station legacy = this.station.toLegacy();

        Date now = new Date();
        legacy.lastUpdate = (now.getTime() - lastUpdate.getTime()) / 1000;

        return legacy;
    }

    @Override
    public String toString() {
        return "Record{" +
                "station=" + station +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
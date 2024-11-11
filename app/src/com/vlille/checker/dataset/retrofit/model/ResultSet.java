package com.vlille.checker.dataset.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultSet {
    @SerializedName("records")
    public final List<Station> records;

    public ResultSet(List<Station> records) {
        this.records = records;
    }

    public List<com.vlille.checker.model.Station> toLegacyStations() {
        if (this.records == null || this.records.isEmpty()) {
            return Collections.emptyList();
        }

        List<com.vlille.checker.model.Station> legacies = new ArrayList<>();

        for (Station record : records) {
            legacies.add(record.toLegacy());
        }

        return legacies;
    }

    public com.vlille.checker.model.Station getFirstStationLegacy() {
        if (this.records == null || this.records.isEmpty()) {
            return null;
        }

        return this.records.get(0).toLegacy();
    }
}
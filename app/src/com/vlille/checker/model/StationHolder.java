package com.vlille.checker.model;

import java.io.Serializable;

public class StationHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Station station;
    private final int index;

    public StationHolder(Station station, int index) {
        this.station = station;
        this.index = index;
    }

    public Station getStation() {
        return station;
    }

    public int getIndex() {
        return index;
    }
}

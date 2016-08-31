package com.vlille.checker.model;

import java.io.Serializable;

public class StationHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    private Station station;
    private int index;
    private boolean initialStar;

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

    public void storeInitialStarValue() {
        this.initialStar = this.station.isStarred();
    }

    public boolean isStarredChanged() {
        return this.station.isStarred() != initialStar;
    }

}

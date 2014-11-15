package com.vlille.checker.xml;

import com.vlille.checker.model.SetStationsInfo;
import com.vlille.checker.model.Station;

import java.util.List;

public class XML {

    /**
     * Gets the station details.
     *
     * @param station The station.
     * @return The parsed station.
     */
    public Station getRemoteInfo(Station station) {
        return OneStation.read(station);
    }

    /**
     * Retrieve all stations information from the remote vlille xml.
     *
     * @return The set with metadata and stations. <code>null</code> if exception was thrown.
     */
    public List<Station> getRemoteStations() {
        return AllStations.readRemote();
    }

    /**
     * Retrieve all stations information from the local asset xml.
     *
     * @return The set with metadata and stations. <code>null</code> if exception was thrown.
     */
    public SetStationsInfo getAssetsStationsInfo() {
        return AllStations.readLocal();
    }

}

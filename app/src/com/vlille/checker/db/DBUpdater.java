package com.vlille.checker.db;

import android.util.Log;
import com.vlille.checker.model.Station;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUpdater extends DBAction {

    private static final String TAG = DBUpdater.class.getSimpleName();

    /**
     * Parse vlille stations and compare stations with those from db, doing a disjunction
     * between the remotes and the local stations. The new stations are created.
     *
     * @return <code>true</code> if new stations have been inserted.
     */
    public boolean update() {
        List<Station> remoteStations = getRemoteStations();
        if (remoteStations == null) {
            return false;
        }

        List<Station> inDBStations = getInDBStations();
        if (inDBStations.isEmpty()) {
            Log.d(TAG, "Create remote remote stations, no stations in db");
            createRemoteStations(remoteStations);

            return true;
        }

        remoteStations.removeAll(inDBStations);
        if (remoteStations.isEmpty()) {
            Log.i(TAG, "Everything seems up to date");
            return false;
        }

        Log.d(TAG, "Check if some stations need to be updated");
        checkForStationsToUpdate(remoteStations, inDBStations);
        createRemoteStations(remoteStations);

        return remoteStations.size() > 0;
    }

    private void checkForStationsToUpdate(List<Station> remoteStations, List<Station> inDBStations) {
        List<Station> toUpdateStations = new ArrayList<>();
        Map<Long, Station> mapInDbStations = toMap(inDBStations);

        for (Station station : remoteStations) {
            Station inDbStation = mapInDbStations.get(station.getId());
            if (inDbStation != null) {
                Log.d(TAG, "Station to update: " + station);
                toUpdateStations.add(station);
            }
        }

        remoteStations.removeAll(toUpdateStations);
        getStationEntityManager().update(toUpdateStations);
    }

    private void createRemoteStations(List<Station> remoteStations) {
        getStationEntityManager().create(remoteStations);
        getMetadataEntityManager().changeLastUpdateToNow();
    }

    private Map<Long, Station> toMap(List<Station> inDBStations) {
        Map<Long, Station> map = new HashMap<>();

        for (Station station : inDBStations) {
            map.put(station.id, station);
        }

        return map;
    }
}


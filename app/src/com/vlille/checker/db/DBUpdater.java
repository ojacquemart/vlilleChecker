package com.vlille.checker.db;

import android.content.Context;
import android.util.Log;

import com.vlille.checker.model.Station;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class DBUpdater extends DBAction {

    private static final String TAG = DBUpdater.class.getSimpleName();

    public DBUpdater(Context context) {
        super(context);
    }

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

        List<Station> inDBStations = getStationEntityManager().findAll();
        final List<Station> newStations = (List<Station>) CollectionUtils.disjunction(remoteStations, inDBStations);
        Log.i(TAG, "New stations " + newStations.size());

        getStationEntityManager().create(newStations);
        getMetadataEntityManager().changeLastUpdateToNow();

        return newStations.size() > 0;
    }
}

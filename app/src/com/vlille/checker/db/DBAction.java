package com.vlille.checker.db;

import android.content.Context;

import com.vlille.checker.Application;
import com.vlille.checker.dataset.StationRepository;
import com.vlille.checker.model.Station;

import java.util.List;

public abstract class DBAction {

    private Context context;
    private StationEntityManager stationEntityManager;
    private MetadataEntityManager metadataEntityManager;

    public DBAction() {
        this.context = Application.getContext();
    }

    public Context getContext() {
        return context;
    }

    public List<Station> getInDBStations() {
        return getStationEntityManager().findAll();
    }

    public List<Station> getRemoteStations() {
        return StationRepository.getStations();
    }

    public StationEntityManager getStationEntityManager() {
        if (stationEntityManager == null) {
            stationEntityManager = new StationEntityManager(context);
        }

        return stationEntityManager;
    }

    public MetadataEntityManager getMetadataEntityManager() {
        if (metadataEntityManager == null) {
            metadataEntityManager = new MetadataEntityManager(context);
        }

        return metadataEntityManager;
    }
}


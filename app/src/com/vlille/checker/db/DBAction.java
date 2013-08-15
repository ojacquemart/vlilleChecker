package com.vlille.checker.db;

import android.content.Context;

import com.vlille.checker.model.SetStationsInfo;
import com.vlille.checker.model.Station;
import com.vlille.checker.xml.XMLReader;

import java.util.List;

public abstract class DBAction {

    private Context context;
    private StationEntityManager stationEntityManager;
    private MetadataEntityManager metadataEntityManager;

    private static final XMLReader XML_READER = new XMLReader();

    public DBAction(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public SetStationsInfo getAssetsStationsInfo() {
        return XML_READER.getAssetsStationsInfo(getContext());
    }

    public List<Station> getRemoteStations() {
        return XML_READER.getRemoteStations();
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

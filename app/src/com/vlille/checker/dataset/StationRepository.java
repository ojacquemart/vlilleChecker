package com.vlille.checker.dataset;

import android.util.Log;

import com.vlille.checker.dataset.retrofit.VlilleClient;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.SetStationsInfo;
import com.vlille.checker.model.Station;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StationRepository {

    private static final String TAG = StationRepository.class.getSimpleName();

    private static final Map<Long, Station> CACHE = new ConcurrentHashMap<>();

    public static SetStationsInfo getSetStationsInfo() {
        try {
            List<Station> stations = getStations();
            if (stations == null || stations.isEmpty()) {
                return null;
            }

            Metadata metadata = new Metadata();
            metadata.setLastUpdate(System.currentTimeMillis());

            return new SetStationsInfo(metadata, stations);
        } catch (Exception e) {
            Log.e(TAG, "Failed to read the local stations list", e);

            return null;
        }
    }

    public static List<Station> getStations() {
        return VlilleClient.getStations();
    }

    public static void fillStationsCache() {
        List<Station> stations = getStations();
        if (stations == null || stations.isEmpty()) {
            fillCacheWithNullValues();

            return;
        }

        fillCache(stations);
    }

    private static void fillCache(List<Station> stations) {
        for (Station station : stations) {
            CACHE.put(station.id, station);
        }
    }

    private static void fillCacheWithNullValues() {
        if (!CACHE.isEmpty()) {
            for (Map.Entry<Long, Station> entry : CACHE.entrySet()) {
                // it is necessary to reset the value null to display empty values
                // when the stations list fetch has maybe failed
                entry.setValue(null);
            }
        }
    }

    public static Station getStation(Station station) {
        Station remoteStation = VlilleClient.getStation(station.id);

        return updateStation(station, remoteStation);
    }

    public static Station getStationFromCache(Station station) {
        Station remoteStation = CACHE.get(station.id);

        return updateStation(station, remoteStation);
    }

    private static Station updateStation(Station station, Station remoteStation) {
        if (remoteStation == null) {
            station.setFetchInError();
            station.setAttachs(null);
            station.setBikes(null);

            return station;
        }

        station.setFetchOk();
        station.setAdress(remoteStation.adress);
        station.setBikes(remoteStation.bikes);
        station.setAttachs(remoteStation.attachs);
        station.setOufOfService(remoteStation.outOfService);
        station.setCbPaiement(remoteStation.cbPaiement);
        station.setLastUpdate(remoteStation.lastUpdate);

        return station;
    }

}

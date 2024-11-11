package com.vlille.checker.dataset.retrofit;

import android.util.Log;

import com.vlille.checker.dataset.retrofit.model.ResultSet;
import com.vlille.checker.dataset.retrofit.model.Station;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

// This class should be temporary.
// It fetches data from opendata & remap objects to the legacy ones provided by the old
// transpole api.
public class VlilleClient {

    public static final String TAG = VlilleClient.class.getSimpleName();
    private static final String JSON_FORMAT = "json";
    private static final int NO_RESULT_LIMIT = -1;
    private static final int ONE_RESULT_LIMIT = 1;
    public static final String FILTER_LANG = "ecql-text";

    public static List<com.vlille.checker.model.Station> getStations() {
        try {
            VlilleService service = getService();
            Call<ResultSet> call = service.getStations(JSON_FORMAT, NO_RESULT_LIMIT, "etat='" + Station.EN_SERVICE + "'", "ecql-text");

            ResultSet resultSet = call.execute().body();
            if (resultSet == null) {
                return Collections.emptyList();
            }

            return resultSet.toLegacyStations();
        } catch (Exception e) {
            Log.e(TAG, "Error while fetching stations list", e);

            return Collections.emptyList();
        }
    }

    public static com.vlille.checker.model.Station getStation(String stationName) {
        try {
            VlilleService service = getService();
            Call<ResultSet> call = service.getStation("json", ONE_RESULT_LIMIT, "nom='" + stationName + "'", FILTER_LANG);

            ResultSet resultSet = call.execute().body();
            if (resultSet == null) {
                return null;
            }

            return resultSet.getFirstStationLegacy();
        } catch (Exception e) {
            Log.e(TAG, "Error while fetching station: " + stationName, e);

            return null;
        }
    }

    private static VlilleService getService() {
        return VlilleService.Factory.INSTANCE.getService();
    }

}

package com.vlille.checker.dataset.retrofit;

import android.util.Log;

import com.vlille.checker.BuildConfig;
import com.vlille.checker.dataset.retrofit.model.ResultSet;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

import static com.vlille.checker.dataset.retrofit.VlilleService.Factory.VLILLE_REALTIME;

// This class should be temporary.
// It fetches data from opendata & remap objects to the legacy ones provided by the old
// transpole api.
public class VlilleClient {

    public static final String TAG = VlilleClient.class.getSimpleName();

    private static final int VLILLE_ROWS = 300;

    public static List<com.vlille.checker.model.Station> getStations() {
        try {
            VlilleService service = getService();
            Call<ResultSet> call = service.getStations(
                    VLILLE_REALTIME,
                    VLILLE_ROWS,
                    BuildConfig.OPENDATA_MEL_APIKEY);

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

    public static com.vlille.checker.model.Station getStation(long stationId) {
        try {
            VlilleService service = getService();
            Call<ResultSet> call = service.getStation(VLILLE_REALTIME, "libelle:" + stationId,
                    BuildConfig.OPENDATA_MEL_APIKEY);

            ResultSet resultSet = call.execute().body();
            if (resultSet == null) {
                return null;
            }

            return resultSet.getFirstStationLegacy();
        } catch (Exception e) {
            Log.e(TAG, "Error while fetching station: " + stationId, e);

            return null;
        }
    }

    private static VlilleService getService() {
        return VlilleService.Factory.INSTANCE.getService();
    }

}

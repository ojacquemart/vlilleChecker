package com.vlille.checker.xml;

import android.text.TextUtils;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.utils.Constants;

import org.jsoup.nodes.Document;

public class OneStation {

    public static final String ADRESS = "adress";
    public static final String STATUS = "status";
    public static final String BIKES = "bikes";
    public static final String ATTACHS = "attachs";
    public static final String PAIEMENT = "paiement";
    public static final String LASTUPD = "lastupd";

    private static final String TAG = OneStation.class.getSimpleName();
    /**
     * Marker for station allowing payment by credit card.
     */
    private static final String FLAG_ALLOWS_CB = "AVEC_TPE";

    /**
     * Marker for station out of service = 1, available = 0.
     */
    private static final String FLAG_OUT_OF_SERVICE = "1";

    public static Station read(Station station) {
        Log.d(TAG, "Gets details for born " + station.getName());
        long start = System.currentTimeMillis();

        String url = Constants.URL_STATION_DETAIL + station.id;

        try {
            Document document = Jsoup.getDocument(url);

            update(station, document);

        } catch (Exception e) {
            Log.e(TAG, "Failed to read the born xml", e);

            station.setFetchInError();
            station.setAttachs(null);
            station.setBikes(null);
        }

        long duration = System.currentTimeMillis() - start;
        Log.d(TAG, "Update in " + duration + " ms");

        return station;
    }

    private static void update(Station station, Document document) {
        station.setFetchOk();
        station.setAdress(document.select(ADRESS).text());
        station.setBikes(document.select(BIKES).text());
        station.setAttachs(document.select(ATTACHS).text());

        String status = document.select(STATUS).text();
        station.setOufOfService(FLAG_OUT_OF_SERVICE.equals(status));

        String paiement = document.select(PAIEMENT).text();
        station.setCbPaiement(FLAG_ALLOWS_CB.equals(paiement));

        String lastupd = document.select(LASTUPD).text();
        station.setLastUpdate(getLastUpdate(lastupd));
    }

    private static long getLastUpdate(String lastupd) {
        if (TextUtils.isEmpty(lastupd)) {
            return 0;
        }

        final Long valueOfLastUpdated = Long.valueOf(lastupd.replaceAll("[^\\d]", "").trim());
        if (valueOfLastUpdated == null) {
            return 0;
        }

        return valueOfLastUpdated;
    }

}

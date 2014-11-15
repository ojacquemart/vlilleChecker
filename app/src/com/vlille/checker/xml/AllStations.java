package com.vlille.checker.xml;

import android.util.Log;

import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.SetStationsInfo;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.Constants;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.vlille.checker.ui.osm.PositionTransformer.to1e6;

public class AllStations {

    public static final String MARKERS = "markers";
    public static final String CENTER_LAT = "center_lat";
    public static final String CENTER_LNG = "center_lng";

    public static final String MARKER = "marker";
    public static final String ID = "id";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String NAME = "name";

    private static final String TAG = AllStations.class.getSimpleName();

    public static List<Station> readRemote() {
        try {
            return AllStations.getList(Jsoup.getDocument(Constants.URL_STATIONS_LIST));
        } catch (Exception e) {
            Log.e(TAG, "Failed to read the remote stations list", e);

            return null;
        }
    }

    public static SetStationsInfo readLocal() {
        try {
            return AllStations.getSetStationsInfo(Jsoup.getDocument(Constants.LOCAL_STATIONS_LIST_FILE_NAME));
        } catch (Exception e) {
            Log.e(TAG, "Failed to read the local stations list", e);

            return null;
        }
    }

    public static List<Station> getList(Document document) {
        Elements markers = document.select(MARKER);

        List<Station> stations = new ArrayList<Station>();

        for (Element element : markers) {
            stations.add(readStation(element));
        }

        return stations;
    }

    public static SetStationsInfo getSetStationsInfo(Document document) {
        Metadata metadata = getMetadata(document);
        List<Station> stations = getList(document);

        return new SetStationsInfo(metadata, stations);
    }

    private static Station readStation(Element element) {
        Station station = new Station();
        station.id = Long.valueOf(element.attr(ID));
        station.setName(element.attr(NAME));

        String lat = element.attr(LAT);
        station.setLatitude(Double.valueOf(lat));
        station.setLatitudeE6(to1e6(lat));

        String lng = element.attr(LNG);
        station.setLongitude(Double.valueOf(lng));
        station.setLongitudeE6(to1e6(lng));

        return station;
    }

    private static Metadata getMetadata(Document document) {
        Elements rootMarkers = document.select(MARKERS);

        Metadata metadata = new Metadata();
        metadata.setLastUpdate(System.currentTimeMillis());
        metadata.setLatitude1e6(to1e6(rootMarkers.attr(CENTER_LAT)));
        metadata.setLongitude1e6(to1e6(rootMarkers.attr(CENTER_LNG)));

        return metadata;
    }

}

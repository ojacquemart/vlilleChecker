package com.vlille.checker.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapStationWidget {
    public static final String TAG = MapStationWidget.class.getSimpleName();
    private Map<Integer, Station> map;

    public MapStationWidget(List<Station> list) {
        this.map = new HashMap<>();
        for (Station station : list) {
            map.put(station.getAppWidgetId(), station);
        }
    }

    public Map<Integer, Station> getMap() {
        return map;
    }

    public Station get(int appWidgetId) {
        return map.get(appWidgetId);
    }
}

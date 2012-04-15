package com.vlille.checker.xml.loader;

import java.util.List;

import android.content.Context;

import com.vlille.checker.model.Station;

public interface StationsLoader {

	void initAll(Context context);
	Station initSingleStation(String stationId);
	List<Station> getAllStations();
	List<Station> getDetailledStations();
}

package com.vlille.checker.model;

import java.util.List;

public class SetStationsInfos {

	private final StationsMapsInfos mapsInformations;
	private final List<Station> stations;

	public SetStationsInfos(StationsMapsInfos mapsInformations, List<Station> stations) {
		this.mapsInformations = mapsInformations;
		this.stations = stations;
	}

	public StationsMapsInfos getMapsInfos() {
		return mapsInformations;
	}

	public List<Station> getStations() {
		return stations;
	}

}

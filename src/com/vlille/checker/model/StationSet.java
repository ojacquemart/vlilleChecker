package com.vlille.checker.model;

import java.util.List;

public class StationSet {

	private final StationsMapsInformation mapsInformations;
	private final List<Station> stations;

	public StationSet(StationsMapsInformation mapsInformations, List<Station> stations) {
		this.mapsInformations = mapsInformations;
		this.stations = stations;
	}

	public StationsMapsInformation getMapsInformations() {
		return mapsInformations;
	}

	public List<Station> getStations() {
		return stations;
	}

}

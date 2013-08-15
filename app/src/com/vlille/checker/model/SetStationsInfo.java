package com.vlille.checker.model;

import java.util.List;

public class SetStationsInfo {

	private final Metadata metadata;
	private final List<Station> stations;

	public SetStationsInfo(Metadata metadata, List<Station> stations) {
		this.metadata = metadata;
		this.stations = stations;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public List<Station> getStations() {
		return stations;
	}

}

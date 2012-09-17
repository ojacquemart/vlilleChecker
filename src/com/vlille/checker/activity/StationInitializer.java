package com.vlille.checker.activity;

import java.util.List;

import com.vlille.checker.model.Station;

public interface StationInitializer {
	
	List<Station> getOnCreateStations();

	List<Station> getOnResumeStations();
}

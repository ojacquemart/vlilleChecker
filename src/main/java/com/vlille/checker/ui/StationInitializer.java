package com.vlille.checker.ui;

import java.util.List;

import com.vlille.checker.model.Station;

public interface StationInitializer {
	
	List<Station> getOnCreateStations();

	List<Station> getOnResumeStations();
}

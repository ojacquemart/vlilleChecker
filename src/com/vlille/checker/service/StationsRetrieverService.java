package com.vlille.checker.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vlille.checker.model.Station;

/**
 * Stations Retriever service.
 */
public class StationsRetrieverService extends AbstractRetrieverService {

	public StationsRetrieverService() {
		super(StationsRetrieverService.class.getSimpleName());
	}
	
	public StationsRetrieverService(String name) {
		super(name);
	}

	@Override
	int doService(Intent intent, Bundle bundle) {
		Log.d(LOG_TAG, "doService#" + LOG_TAG);
		
		@SuppressWarnings("unchecked")
		final ArrayList<Station> stationsToLoad = (ArrayList<Station>) intent.getSerializableExtra(EXTRA_DATA);
		final List<Station> stations = new ArrayList<Station>();
		
		for (Station eachStationToLoad : stationsToLoad) {
			checkStationDetails(eachStationToLoad);
			
			stations.add(eachStationToLoad);
		}
		
		bundle.putSerializable(RESULTS, (ArrayList<Station>) stations);
		
		return stations.size();
	}
	


}

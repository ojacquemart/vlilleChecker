package com.vlille.checker.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.service.StationsResultReceiver.Receiver;
import com.vlille.checker.xml.StationXMLLoader;

/**
 * Stations Retriever service.
 */
public class StationsRetrieverService extends IntentService {
	
	public static final String RESULTS = "results";
	public static final String STATIONS_ID = "stationsId";
	
	private static final String LOG_TAG = StationsResultReceiver.class.getSimpleName();
	
	public StationsRetrieverService() {
		super(LOG_TAG);
	}

	public StationsRetrieverService(String name) {
		super(name);
	}

	@Override
	/**
	 * Load stations details.
	 * The stations ids are passed through the extra from the intent.
	 * 
	 * @param intent Intent running.
	 */
	protected void onHandleIntent(Intent intent) {
		Log.d(LOG_TAG, intent.getAction());
		

		final ResultReceiver receiver = intent.getParcelableExtra(Receiver.RECEIVER);
		receiver.send(Receiver.RUNNING, Bundle.EMPTY);
		
		Bundle bundle = new Bundle();
		try {
			final StopWatch watcher = new StopWatch();
			watcher.start();

			@SuppressWarnings("unchecked")
			final ArrayList<Station> stationsIdToLoad = (ArrayList<Station>) intent.getSerializableExtra(STATIONS_ID);
			final List<Station> stations = new ArrayList<Station>();
			
			for (Station eachStationIdToLoad : stationsIdToLoad) {
				Station station = StationXMLLoader.getSingle(eachStationIdToLoad);
				if (station == null) {
					throw new NullPointerException("Station is null");
				}
				
				stations.add(station);
			}

			Collections.sort(stations);
			
			watcher.stop();
			Log.d(LOG_TAG, "Retrieved " + stations.size() + " stations  in " + watcher.getTime() + "ms");

			bundle.putSerializable(RESULTS, (ArrayList<Station>) stations);
			receiver.send(Receiver.FINISHED, bundle);
		} catch (Exception e) {
			bundle.putString(Intent.EXTRA_TEXT, e.toString());
			receiver.send(Receiver.ERROR, bundle);
		}
		
		this.stopSelf();
	}


}

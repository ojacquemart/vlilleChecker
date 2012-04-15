package com.vlille.checker.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.service.StationResultReceiver.Receiver;
import com.vlille.checker.xml.loader.StationsLoader;
import com.vlille.checker.xml.loader.StationsLoaderImpl;

/**
 * Stations Retriever service.
 */
public class StationsRetrieverService extends IntentService {
	
	public static final String RESULTS = "results";
	public static final String STATIONS_ID = "stationsId";
	
	private static final String SERVICE_LOG_TAG = StationResultReceiver.class.getSimpleName();
	
	private StationsLoader loader;

	public StationsRetrieverService() {
		super(SERVICE_LOG_TAG);
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
		Log.d(SERVICE_LOG_TAG, intent.getAction());
		
		loader = new StationsLoaderImpl();
		loader.initAll(getApplicationContext());

		final ResultReceiver receiver = intent.getParcelableExtra(Receiver.RECEIVER);
		receiver.send(Receiver.RUNNING, Bundle.EMPTY);
		
		Bundle bundle = new Bundle();
		try {
			final StopWatch watcher = new StopWatch();
			watcher.start();

			final ArrayList<String> stationsIdToLoad = intent.getStringArrayListExtra(STATIONS_ID);
			final List<Station> stations = new ArrayList<Station>();
			
			for (String eachStationIdToLoad : stationsIdToLoad) {
				Station station = loader.initSingleStation(eachStationIdToLoad);
				if (station == null) {
					throw new NullPointerException("Station is null");
				}
				
				stations.add(station);
			}

			watcher.stop();

			Log.d(SERVICE_LOG_TAG, "Retrieved " + stations.size() + " stations  in " + watcher.getTime() + "ms");

			bundle.putParcelableArrayList(RESULTS, (ArrayList<Station>) stations);
			receiver.send(Receiver.FINISHED, bundle);
		} catch (Exception e) {
			bundle.putString(Intent.EXTRA_TEXT, e.toString());
			receiver.send(Receiver.ERROR, bundle);
		}
		
		this.stopSelf();
	}


}

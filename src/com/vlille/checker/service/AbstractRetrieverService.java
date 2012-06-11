package com.vlille.checker.service;

import org.apache.commons.lang3.time.StopWatch;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.service.StationsResultReceiver.Receiver;
import com.vlille.checker.xml.XMLReader;

public abstract class AbstractRetrieverService extends IntentService {

	public static final String EXTRA_DATA = "toLoad";
	public static final String RESULTS = "results";
	
	private static final XMLReader XML_READER = new XMLReader();
	
	protected final String LOG_TAG = getClass().getSimpleName();
	
	public AbstractRetrieverService(String name) {
		super(name);
	}
	
	/**
	 * Load stations details.
	 * The stations ids are passed through the extra from the intent.
	 * 
	 * @param intent Intent running.
	 */
	@Override
	public void onHandleIntent(Intent intent) {
		Log.d(LOG_TAG, intent.getAction());
		
		final ResultReceiver receiver = intent.getParcelableExtra(Receiver.RECEIVER);
		receiver.send(Receiver.RUNNING, Bundle.EMPTY);
		
		Bundle bundle = new Bundle();
		try {
			doHandleIntent(bundle, intent);
			
			receiver.send(Receiver.FINISHED, bundle);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception occured", e);
			bundle.putString(Intent.EXTRA_TEXT, e.toString());
			receiver.send(Receiver.ERROR, bundle);
		}
		
		this.stopSelf();
	}

	private void doHandleIntent(Bundle bundle, Intent intent) {
		final StopWatch watcher = new StopWatch();
		watcher.start();

		int itemsRetrieved = doService(intent, bundle);
		
		watcher.stop();
		
		Log.d(LOG_TAG, "Retrieved " + itemsRetrieved + " items  in " + watcher.getTime() + "ms");
	}

	/**
	 * Implements this method in your service.
	 * @param intent The intent to get extra data.
	 * @param bundle The bundle to send extra data.
	 * @return
	 */
	abstract int doService(Intent intent, Bundle bundle);
	
	/**
	 * Check if stations need to update its informations.
	 * @param station The station to load.
	 * @return <code>true</code> if the station is up to date. <code>false</code> otherwise
	 */
	public boolean checkStationDetails(Station station) {
		final boolean upToDate = station.isUpToDate();
		if (upToDate) {
			return true;
		}
	
		Station updatedStation = XML_READER.getDetails(station.getId());
		if (updatedStation == null) {
			throw new NullPointerException("Station is null");
		}
		
		station.copyParsedInfos(updatedStation);
		
		return false;
	}

}

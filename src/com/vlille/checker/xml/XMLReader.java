package com.vlille.checker.xml;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import android.util.Log;

import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.Constants;
import com.vlille.checker.xml.detail.StationDetailSAXParser;
import com.vlille.checker.xml.list.StationsListSAXParser;

public class XMLReader {
	
	private static final String LOG_TAG = XMLReader.class.getSimpleName();
	private static final int READ_TIMEOUT = 3000;
	private static final int CONNECTION_TIMEOUT = 3000;

	/**
	 * Update station details. 
	 * If the station retrieved is not null, the station id is updated
	 * and the station is merged in db, in order to optimize the retrieve.
	 * 
	 * @param station The station.
	 * @return The parsed station.
	 */
	public void updateDetails(Station station)  {
		try {
			final String httpUrl = Constants.URL_STATION_DETAIL + station.getId();
			station = new StationDetailSAXParser(station).parse(getInputStream(httpUrl));
		} catch (Exception e) {
			Log.e(LOG_TAG, "getDetails throws an exception", e);
			
			station.setBikes(null);
			station.setAttachs(null);
		}
		
		VlilleChecker.getDbAdapter().update(station);
	}
	
	/**
	 * Retrieve all stations informations.
	 * The retrieve is made asynchronous in ordre to be compatible with the ICS version.
	 * 
	 * @return The set with metadata and stations.
	 */
	public SetStationsInfos getAsyncSetStationsInfos() {
		try {
			return new AsyncFeedReader<SetStationsInfos>(new StationsListSAXParser()).execute(Constants.URL_STATIONS_LIST).get();
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, "InterruptedException", e);
		} catch (ExecutionException e) {
			Log.e(LOG_TAG, "ExecutionException", e);
		}
		
		return null;
	}
	 
	/**
	 * Get input stream from a given http url.
	 * @return The inpustream. <code>null</code> if an exception occured.
	 */
	public InputStream getInputStream(String httpUrl) {
		InputStream inputStream = null;
		
		try {
			final URL url = new URL(httpUrl);
			final URLConnection connection = url.openConnection();
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.connect();
			
			inputStream = connection.getInputStream();
			
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error during xml reading", e);
		}
		
		return inputStream; 
	}
	
}

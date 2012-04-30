package com.vlille.checker.stations.xml;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.vlille.checker.stations.Constants;

import android.util.Log;


public class StationDetailXMLReader {

	private static final String LOG_TAG = StationDetailXMLReader.class.getSimpleName();
	
	private static final int READ_TIMEOUT = 3000;
	private static final int CONNECTION_TIMEOUT = 3000;

	/**
	 * Get input stream.
	 * @return null if an exception occured.
	 */
	public static InputStream getInputStream(String stationId) {
		InputStream inputStream = null;
		
		try {
			final String stationUrl = Constants.URL_STATION_DETAIL + stationId;
			Log.d(LOG_TAG, "Url to load: " + stationUrl);
			
			final URL url = new URL(stationUrl);
			final URLConnection connection = url.openConnection();
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.connect();
			
			inputStream = connection.getInputStream();
			
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error during xml read", e);
		}
		
		return inputStream; 
	}

}

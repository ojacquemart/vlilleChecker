package com.vlille.checker.xml;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.Constants;
import com.vlille.checker.xml.detail.StationDetailSAXParser;
import com.vlille.checker.xml.list.StationsListSAXParser;

public class StationXMLLoader {
	
	private static final String LOG_TAG = StationXMLLoader.class.getSimpleName();
	
	private StationXMLLoader() {
	}
	
	public static Station getSingle(Station station) {
		StationXMLLoader xmlLoader = new StationXMLLoader();
		
		return xmlLoader.loadSingle(station);
	}
	
	private Station loadSingle(Station station)  {
		try {
			final String httpStationUrl = Constants.URL_STATION_DETAIL + station.getId();
			StationDetailSAXParser parser = new StationDetailSAXParser(XMLReader.getInputStream(httpStationUrl));
			Station parsedStation = parser.parse();
			if (parsedStation != null) {
				station.copyParsedInfos(parsedStation);
			}
			
			return station;
		} catch (Throwable e) {
			return null;
		}
	}
	
	public static SetStationsInfos getAll() {
		StationXMLLoader xmlLoader = new StationXMLLoader();
		
		return xmlLoader.loadAll();
	}
	
	private SetStationsInfos loadAll() {
		try {
			final InputStream inputStreamStations = XMLReader.getInputStream(Constants.URL_STATIONS_LIST);
			return new StationsListSAXParser(inputStreamStations).parse();
			
		} catch (Exception e) {
			return null;
		}
	}
	
	public static class XMLReader {
		
		private static final int READ_TIMEOUT = 3000;
		private static final int CONNECTION_TIMEOUT = 3000;

		/**
		 * Get input stream.
		 * @return null if an exception occured.
		 */
		public static InputStream getInputStream(String httpUrl) {
			InputStream inputStream = null;
			
			try {
				Log.d(LOG_TAG, "Url to load: " + httpUrl);
				
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
}

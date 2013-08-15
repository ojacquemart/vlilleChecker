package com.vlille.checker.xml;

import android.content.Context;
import android.util.Log;

import com.vlille.checker.model.SetStationsInfo;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.Constants;
import com.vlille.checker.xml.detail.StationDetailSAXParser;
import com.vlille.checker.xml.list.StationsListSAXParser;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class XMLReader {
	
	private static final String TAG = XMLReader.class.getSimpleName();

	private static final int READ_TIMEOUT = 3000;
	private static final int CONNECTION_TIMEOUT = 3000;

	/**
	 * Gets the station details.
	 *
	 * @param station The station.
	 * @return The parsed station.
	 */
	public Station getRemoteInfo(Station station)  {
		try {
            Log.d(TAG, "Gets details for " + station.getName());

            long start = System.currentTimeMillis();

			final String httpUrl = Constants.URL_STATION_DETAIL + station.id;
			station = new StationDetailSAXParser(station).parse(getInputStream(httpUrl));

            long duration = System.currentTimeMillis() - start;
            Log.d(TAG, "Update in " + duration + " ms");
		} catch (Exception e) {
			Log.e(TAG, "Error during the xml parsing.", e);
			
			station.setBikes(null);
			station.setAttachs(null);
		}

        return station;
	}

    public List<Station> getRemoteStations() {
        final InputStream inputStream = new XMLReader().getInputStream(Constants.URL_STATIONS_LIST);
        if (inputStream == null) {
            return null;
        }

        return new StationsListSAXParser().parse(inputStream).getStations();
    }

	/**
	 * Retrieve all stations information from the local asset xml.
	 * 
	 * @param context the current context.
	 * @return The set with metadata and stations. <code>null</code> if exception was thrown.
	 */
	public SetStationsInfo getAssetsStationsInfo(final Context context) {
		try {
			final InputStream inputStream = context.getAssets().open("vlille_stations.xml");
			
			return new StationsListSAXParser().parse(inputStream);
		} catch (Exception e) {
            Log.e(TAG, "Error during reading vlille_stations.xml", e);

            throw new IllegalStateException("Error during reading vlille_stations.xml", e);
		}
	}
	 
	/**
	 * Gets the InputStream from a given http url.
	 * 
	 * @return The inpustream. <code>null</code> if any exception occured.
	 */
	public InputStream getInputStream(String httpUrl) {
		InputStream inputStream = null;
		
		try {
            Log.d(TAG, "Load url " + httpUrl);

			final URL url = new URL(httpUrl);
			final URLConnection connection = url.openConnection();
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.connect();
			
			inputStream = connection.getInputStream();
			
		} catch (Exception e) {
			Log.e(TAG, "Error during xml reading", e);
		}
		
		return inputStream; 
	}
	
}

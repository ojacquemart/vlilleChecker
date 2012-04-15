package com.vlille.checker.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.util.Log;

import com.vlille.checker.utils.VlilleConstants;

public class StationDetailXMLReader {

	private static final String LOG_TAG_STATION_DETAIL_XML_READER = StationDetailXMLReader.class.getSimpleName();

	/**
	 * Get input stream.
	 * @return null if an exception occured.
	 */
	public static InputStream getInputStream(String stationId) {
		try {
			final String stationUrl = VlilleConstants.STATION_DETAIL.value() + stationId;
			Log.d(LOG_TAG_STATION_DETAIL_XML_READER, "Url to load: " + stationUrl);
			
			return new URL(stationUrl).openStream();
		} catch (IOException e) {
			Log.e(LOG_TAG_STATION_DETAIL_XML_READER, "Error during xml reader: "
					+ e.getMessage() != null 
						? e.getMessage()
						: ""
			);
			
			return null;
		}
	}

}

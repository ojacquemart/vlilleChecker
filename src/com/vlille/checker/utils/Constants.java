package com.vlille.checker.utils;

import java.util.concurrent.TimeUnit;


public class Constants {
		
	/**
	 * ACRA - The google docs form key.
	 */
	public static final String GOOGLE_DOCS_FORM_KEY = "dFFlS2h4TWUxeHdmSGNVYmk1WTJTa2c6MA";
	
	/**
	 * Value when network failed and default value to store stations.
	 */
	public static final String DEFAULT_VALUE = "...";
	
	/**
	 * Duration for keeping data in cache.
	 */
	public static final long CACHE_DATA_DURATION = TimeUnit.SECONDS.toMillis(10);
	
	/**
	 * One minute in milliseconds.
	 */
	public static final int ONE_MINUTE_IN_MILLSECONDS = 60 * 1000;
	
	/**
	 * One day in milliseconds.
	 */
	public static final int ONE_DAY_IN_MILLSECONDS = 60 * 60 * 24 * 1000;
	
	/**
	 * One week in milliseconds
	 */
	public static final int ONE_WEEK_IN_MILLSECONDS = 7 * ONE_DAY_IN_MILLSECONDS;
	
	/**
	 * Local file stations file name.
	 */
	public static final String FILE_NAME = "vlille_stations.xml";
	
	/**
	 * XML path in local, with all stations.
	 */
	public static final String FILE_ASSET_STATIONS_LIST = "xml/" + FILE_NAME;
	
	/**
	 * URL to get stations list.
	 */
	public static final String URL_STATIONS_LIST = "http://www.vlille.fr/stations/xml-stations.aspx";
	
	/**
	 * URL to get detail for one single station.
	 */
	public static final String URL_STATION_DETAIL = "http://www.vlille.fr/stations/xml-station.aspx?borne=";
	
	/**
	 * Marker for station allowing payment by credit card.
	 */
	public static final String FLAG_ALLOWS_CB = "AVEC_TPE";
	
	/**
	 * Marker for station out of service = 1, available = 0.
	 */
	public static final String FLAG_OUT_OF_SERVICE = "1";
	
}

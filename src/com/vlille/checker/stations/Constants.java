package com.vlille.checker.stations;


public class Constants {
		
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
	 * Marker for station out of service.
	 */
	public static final String FLAG_OUT_OF_SERVICE = "0";
	
}

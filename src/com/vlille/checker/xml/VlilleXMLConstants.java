package com.vlille.checker.xml;

/**
 * Ville urls for retrieving xml data.
 */
public enum VlilleXMLConstants {

	/**
	 * XML path in local, with all stations.
	 */
	STATIONS_XML_PATH("xml/vlille_stations.xml"),
	/**
	 * XML with all stations.
	 */
	STATIONS_LIST("http://www.vlille.fr/stations/xml-stations.aspx"),
	/**
	 * XML for one single station.
	 */
	STATION_DETAIL("http://www.vlille.fr/stations/xml-station.aspx?borne="),
	/**
	 * Marker for paiement by credit card.
	 */
	STATION_WITH_CB("AVEC_TPE"),
	/**
	 * Marker for out of service station.
	 */
	STATION_OUT_OF_SERVICE("0");
	
	private String key;
	
	private VlilleXMLConstants(String key) {
		this.key = key;
	}
	
	public String value() {
		return this.key;
	}
}

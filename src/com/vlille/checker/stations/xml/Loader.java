package com.vlille.checker.stations.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.content.Context;

import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ContextHelper;

public class Loader {
	
	private List<Station> stations = new ArrayList<Station>();
	private List<Station> allStations = new ArrayList<Station>();
	
	private Context context;
	
	public void initAll(Context context) {
		this.context = context;
		
		InputStream inputStream = ContextHelper.getInputStream(this.context);
		allStations = new StationsListSAXParser(inputStream).parse().getStations();
	}
	
	public Station initSingleStation(String stationId)  {
		try {
			Station parsedStation = parse(StationDetailXMLReader.getInputStream(stationId));
			if (parsedStation != null) {
				parsedStation.setId(stationId);
				
				/**
				 * Complete with name from all stations.
				 * The detailed xml doesn't have the full name.
				 */
				int indexOfParsedStation = allStations.indexOf(parsedStation);
				if (indexOfParsedStation != -1) {
					String name = allStations.get(indexOfParsedStation).getName();
					parsedStation.setName(name);
				}
			}
			
			return parsedStation;
		} catch (Throwable e) {
			return null;
		}
	}
	
	
//	@Override
//	public void initStations(List<String> stationIds) {
//		for (String eachStationId : stationIds) {
//			final Station station = initSingleStation(eachStationId);
//			if (station != null) {
//				stations.add(station);
//			}
//		}
//		
//		Collections.sort(stations);
//	}
	
	private Station parse(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
		StationDetailSAXParser parser = new StationDetailSAXParser(inputStream);
		
		return parser.parse();
	}
	
	public List<Station> getAllStations() {
		return allStations;
	}

	public List<Station> getDetailledStations() {
		return stations;
	}
	
}

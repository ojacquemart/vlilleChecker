package com.vlille.checker.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.util.Log;

import com.vlille.checker.activity.HomeActivity;
import com.vlille.checker.model.Station;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.stations.xml.StationDetailSAXParser;
import com.vlille.checker.stations.xml.StationDetailXMLReader;
import com.vlille.checker.stations.xml.StationsListSAXParser;

public class TestStationParser extends AbstractVlilleTest<HomeActivity> {

	public TestStationParser() {
		super(HomeActivity.class);
	}
	
	public void testStationsListHandler() {
		InputStream isVlilleStation = null;
		try {
			isVlilleStation = getInputStream();
		} catch (IOException e) {
			Log.e(LOG_TAG, "ioException", e);
		}

		assertNotNull(isVlilleStation);

		StationsListSAXParser stationParser = new StationsListSAXParser(isVlilleStation);
		final SetStationsInfos stationSet = stationParser.parse();
		assertTrue(stationSet.getMapsInfos().getLatitude1e6() > 0);
		assertTrue(stationSet.getMapsInfos().getLongitude1e6() > 0);
		assertTrue(stationSet.getMapsInfos().getZoom() > 0);

		List<Station> stations = stationSet.getStations();
		assertNotNull(stations);
		assertTrue(!stations.isEmpty());

		Station firstStation = stations.get(0);
		assertNotNull(firstStation.getId());
		assertNotNull(firstStation.getName());
		assertTrue(firstStation.getLatitudeE6() > 0);
		assertTrue(firstStation.getLongituteE6() > 0);
	}

	public void testStationDetailLocal() {
		InputStream isVlilleStation = null;
		try {
			isVlilleStation = getActivity().getApplicationContext().getAssets()
					.open("xml/vlille_station_details_example.xml");
		} catch (IOException e) {
			Log.e(LOG_TAG, "ioException", e);
		}
		assertNotNull(isVlilleStation);

		Station station = parseStation(isVlilleStation);

		assertStationDetail(station);
	}

	public void testStationDetailRemote() {
		InputStream inputStream = StationDetailXMLReader.getInputStream("110");
		assertNotNull(inputStream);

		Station station = parseStation(inputStream);

		assertStationDetail(station);
	}

	private Station parseStation(InputStream inputStream) {
		StationDetailSAXParser stationParser = new StationDetailSAXParser(inputStream);

		try {
			return stationParser.parse();
		} catch (Exception e) {
			Log.e(LOG_TAG, "exception", e);
		}

		return null;
	}

	private void assertStationDetail(Station station) {
		assertNotNull(station);
		assertNotNull(station.getAdress());
		assertNotNull(station.getBikes());
		assertNotNull(station.getAttachs());
		assertNotNull(station.isCbPaiement());
		assertNotNull(station.isOutOfService());
	}

	private InputStream getInputStream() throws IOException {
		return getActivity().getApplicationContext().getAssets().open("xml/vlille_stations.xml");
	}
	
}

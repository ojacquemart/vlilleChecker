package com.vlille.checker.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.util.Log;

import com.vlille.checker.activity.HomeActivity;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.Station;
import com.vlille.checker.xml.XMLReader;
import com.vlille.checker.xml.detail.StationDetailSAXParser;
import com.vlille.checker.xml.list.StationsListSAXParser;

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

		final SetStationsInfos stationSet = new StationsListSAXParser().parse(isVlilleStation);
		assertTrue(stationSet.getMetadata().getLatitude1e6() > 0);
		assertTrue(stationSet.getMetadata().getLongitude1e6() > 0);

		List<Station> stations = stationSet.getStations();
		assertNotNull(stations);
		assertTrue(!stations.isEmpty());

		Station firstStation = stations.get(0);
		assertNotNull(firstStation.getId());
		assertNotNull(firstStation.getName());
		assertTrue(firstStation.getLatitudeE6() > 0);
		assertTrue(firstStation.getLongituteE6() > 0);
	}
	
	public void testLoadAllStations() {
		final SetStationsInfos setStations = new XMLReader().getSetStationsInfos();
		assertNotNull(setStations);
		assertNotNull(setStations.getMetadata());
		assertNotNull(setStations.getStations());
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

	private Station parseStation(InputStream inputStream) {
		try {
			return new StationDetailSAXParser().parse(inputStream);
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

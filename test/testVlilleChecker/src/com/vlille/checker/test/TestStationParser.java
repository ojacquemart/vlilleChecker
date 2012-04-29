package com.vlille.checker.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.vlille.checker.activity.HomeActivity;
import com.vlille.checker.model.Station;
import com.vlille.checker.model.StationSet;
import com.vlille.checker.xml.StationDetailSAXParser;
import com.vlille.checker.xml.StationDetailXMLReader;
import com.vlille.checker.xml.StationsListSAXParser;

public class TestStationParser extends
		ActivityInstrumentationTestCase2<HomeActivity> {

	private final String LOG_TAG = getClass().getSimpleName();
	
	private static final String ACTIVITY_PACKAGE_NAME = "com.vlille.checker.activity";
	
	private HomeActivity activity;

	public TestStationParser() {
		super(ACTIVITY_PACKAGE_NAME, HomeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		activity = getActivity();
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
		final StationSet stationSet = stationParser.parse();
		assertTrue(stationSet.getMapsInformations().getLatitude1e6() > 0);
		assertTrue(stationSet.getMapsInformations().getLongitude1e6() > 0);
		assertTrue(stationSet.getMapsInformations().getZoom() > 0);
		
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
			isVlilleStation = activity.getApplicationContext().getAssets().open("xml/vlille_station_details_example.xml");
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
		assertNotNull(station.getPaiement());
		assertNotNull(station.getStatus());
	}
	
	private InputStream getInputStream() throws IOException {
		return activity.getApplicationContext().getAssets().open("xml/vlille_stations.xml");
	}	
}

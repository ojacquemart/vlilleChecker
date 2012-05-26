package com.vlille.checker.test;

import java.util.List;

import android.content.Context;

import com.vlille.checker.activity.HomeActivity;
import com.vlille.checker.db.DbAdapter;
import com.vlille.checker.db.DbSchema;
import com.vlille.checker.model.Station;
import com.vlille.checker.stations.xml.Loader;

public class TestDbQueries extends AbstractVlilleTest<HomeActivity> {

	private DbAdapter dbAdapter;
	
	public TestDbQueries() {
		super(HomeActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		final Context context = getActivity().getApplicationContext();
		context.deleteDatabase(DbSchema.DB_NAME);
		
		dbAdapter = new DbAdapter(context);
		dbAdapter.unstarAll();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		dbAdapter.close();
	}
	
	public void testStarAndUnstar() {
		dbAdapter.star(true, buildStationWithId(4L));
		dbAdapter.star(true, buildStationWithId(2L));
		
		List<Station> stations = dbAdapter.getStarredStations();
		assertNotNull(stations);
		assertEquals(2, stations.size());
		
		dbAdapter.star(false, buildStationWithId(4L));
		
		stations = dbAdapter.getStarredStations();
		assertNotNull(stations);
		assertEquals(1, stations.size());
		
		dbAdapter.star(false, buildStationWithId(2L));
		
		stations = dbAdapter.getStarredStations();
		assertNotNull(stations);
		assertTrue(stations.isEmpty());
	}
	
	public void testUpdateStation() {
		Station station = dbAdapter.find(1L);
		assertNotNull(station);
		
		// Adress must be null at the db initialization.
		assertNull(station.getAdress());
		
		final Station detailledStation = new Loader().initSingleStation(station.getId());
		assertNotNull(detailledStation);
		assertNotNull(detailledStation.getAdress());
		dbAdapter.update(detailledStation);
		station = dbAdapter.find(1L);
		
		// Adress has been filled.
		assertNotNull(station.getAdress());
	}
	
	private Station buildStationWithId(Long id) {
		Station station = new Station();
		station.setId(id.toString());
		
		return station;
	}
	
}

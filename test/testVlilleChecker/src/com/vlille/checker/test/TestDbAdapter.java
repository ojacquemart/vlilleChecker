package com.vlille.checker.test;

import java.util.List;

import android.content.Context;

import com.vlille.checker.activity.HomeActivity;
import com.vlille.checker.db.DbAdapter;
import com.vlille.checker.db.DbSchema;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.Constants;
import com.vlille.checker.xml.XMLReader;

public class TestDbAdapter extends AbstractVlilleTest<HomeActivity> {

	private DbAdapter dbAdapter;
	
	public TestDbAdapter() {
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
	
	public void testCheckUpdate() {
		final long lastUpdate = System.currentTimeMillis() - (7 * Constants.ONE_DAY_IN_MILLSECONDS) - 1;
		dbAdapter.changeLastUpdate(lastUpdate);
		assertTrue(dbAdapter.wasUpdatedMoreThanOneWeekAgo(lastUpdate));

		int beforeUpdateNbStations = dbAdapter.findAll().size();
		
		// Delete first station to simulation disjunction between existing stations and new stations.
		dbAdapter.deleteStation(1L);
		
		dbAdapter.checkIfNeedsUpdate();
		final int afterUpdateNbStations = dbAdapter.findAll().size();
		assertTrue(afterUpdateNbStations >= beforeUpdateNbStations);
	}
	
	public void testFindAll() {
		final List<Station> stations = dbAdapter.findAll();
		assertNotNull(stations);
		assertFalse(stations.isEmpty());
	}
	
	public void testFindMetadata() {
		final Metadata metada = dbAdapter.findMetadata();
		assertNotNull(metada);
		assertNotNull(metada.getLastUpdate());
		assertNotNull(metada.getLatitude1e6());
		assertNotNull(metada.getLongitude1e6());
	}
	
	public void testStarAndUnstar() {
		dbAdapter.star(true, buildStationWithId(4L));
		dbAdapter.star(true, buildStationWithId(2L));
		
		List<Station> stations = dbAdapter.getStarredStations();
		assertNotNull(stations);
		assertEquals(2, stations.size());
		
		dbAdapter.unstar(buildStationWithId(4L));
		
		stations = dbAdapter.getStarredStations();
		assertNotNull(stations);
		assertEquals(1, stations.size());
		
		dbAdapter.unstar(buildStationWithId(2L));
		
		stations = dbAdapter.getStarredStations();
		assertNotNull(stations);
		assertTrue(stations.isEmpty());
	}
	
	public void testIsStarred() {
		dbAdapter.star(buildStationWithId(2L));
		
		final List<Station> starredStations = dbAdapter.getStarredStations();
		final Station station = starredStations.get(0);
		
		assertTrue(dbAdapter.isStarred(station));
		
		dbAdapter.unstar(station);
		
		assertFalse(dbAdapter.isStarred(station));
	}
	
	public void testUpdateStation() {
		Station station = dbAdapter.find(1L);
		assertNotNull(station);
		
		// Adress must be null at the db initialization.
		assertNull(station.getAdress());
		
		final Station detailledStation = new XMLReader().getDetails(station.getId());
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

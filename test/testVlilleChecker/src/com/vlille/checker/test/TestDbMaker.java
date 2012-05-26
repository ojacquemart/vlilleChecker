package com.vlille.checker.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.vlille.checker.db.Field;
import com.vlille.checker.db.StationTable;
import com.vlille.checker.db.Type;
import com.vlille.checker.db.DbSchema;

/**
 * Run the test in deleting the android library from the classpath.
 * @see http://stackoverflow.com/questions/2543106/fatal-error-by-java-runtime-environment/3223929#3223929
 */
public class TestDbMaker {

	private static final String EXCEPTED_SQL_STATION_TABLE = "CREATE TABLE station (" +
			"_id INTEGER primary key not null," +
			"suggest_text_1 STRING not null," +
			"latitude REAL not null," +
			"longitude REAL not null," +
			"latitudeE6 REAL not null," +
			"longitudeE6 REAL not null," +
			"adress STRING," +
			"bikes INTEGER," +
			"attachs INTEGER," +
			"cbPaiement INTEGER," +
			"outOfService INTEGER," +
			"lastUpdate INTEGER," +
			"starred INTEGER," +
			"ordinal INTEGER" +
			");";
	private static final String EXCEPTED_SQL_DATABASE =
			EXCEPTED_SQL_STATION_TABLE +
			"CREATE TABLE map_infos (" +
			"latitude INTEGER not null," +
			"longitude INTEGER not null" +
			");";

	@Test
	public void testGenerateFields() {
		System.out.println(TestDbMaker.class.getPackage().getName());
		final Field idField = Field.newField("id").type(Type.INTEGER).primaryKey().autoIncrement();
		assertField(idField, "id INTEGER primary key autoincrement not null");
		
		final Field nameField = Field.newField("name").type(Type.STRING).notNullable();
		assertField(nameField, "name STRING not null");
		
		final Field adressField = Field.newField("adress").type(Type.STRING).nullable();
		assertField(adressField, "adress STRING");
		
		// To check chaining call methods order.
		final Field starredField = Field.newField("starred").nullable().type(Type.INTEGER).autoIncrement();
		assertField(starredField, "starred INTEGER autoincrement");
	}
	
	private void assertField(Field field, String exceptedSql) {
		assertEquals(exceptedSql, field.toString());
	}
	
	@Test
	public void testGenerateTable() {
		final StationTable stationTable = new StationTable();
		
		try {
			stationTable.toString();
		} catch (IllegalStateException e) {
			fail();
		}
		
		final String sqlStationStable = stationTable.toString();
		assertEquals(EXCEPTED_SQL_STATION_TABLE, sqlStationStable);
	}
	
	@Test
	public void testGenerateDataBase() {
		final String sqlTables = StringUtils.join(new DbSchema().getTables(), "");
		assertEquals(EXCEPTED_SQL_DATABASE, sqlTables);
	}
}

package com.vlille.checker.db;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.Station;
import com.vlille.checker.model.StationsMapsInfos;
import com.vlille.checker.utils.ContextHelper;

/**
 * Adapter with helper methods to query the database.
 */
public class DbAdapter {

	private final String LOG_TAG = getClass().getSimpleName();

	private SQLiteDatabase db;
	private VlilleOpenHelper helper;
	private Context context;
	
	public DbAdapter(Context context) {
		this.context = context;
		this.helper = new VlilleOpenHelper(context);
		this.db = helper.getWritableDatabase();
	}

	public SQLiteDatabase getReadableDatabase() {
		return helper.getReadableDatabase();
	}
	
	public void close() {
		helper.close();
	}
	
	// Stations queries...
	
	/**
	 * Retrieve single station.
	 * @param id The station id.
	 * @return The station from the db.
	 */
	public Station find(Long id) {
		return search(StationTableFields._id + "=" + id.toString(), null).get(0);
	}
	
	/**
	 * Retrieve all stations.
	 * @return The stations from the db.
	 */
	public List<Station> findAll() {
		return search(null, StationTableFields.suggest_text_1.toString());
	}
	
	/**
	 * Search the starred stations.
	 * @return The starred stations ordered by name.
	 */
	public List<Station> getStarredStations() {
		return search(StationTableFields.starred + "=1", StationTableFields.suggest_text_1.toString());
	}
	
	private List<Station> search(String where, String order) {
		final Cursor cursor = db.query(StationTable.TABLE_NAME,
				StationTableFields.getProjection(),
				where, null, null, null, order);
		
		Log.d(LOG_TAG, "Nb stations: " + cursor.getCount());
		return StationCursorTransformer.transform(cursor).all();
	}
	
	/**
	 * Star or unstar one single station.
	 * @param star The starred value.
	 * @param stationId The station id.
	 */
	public void star(boolean star, Station station) {
		updateStation(getStarredValues(star), station);
	}
	
	public void star(boolean star, String stationId) {
		updateStation(getStarredValues(star), new Station(stationId));
	}
	
	/**
	 * Get starred values to update.
	 * @param star the starred value.
	 * @return the values to update.
	 */
	private ContentValues getStarredValues(boolean star) {
		final ContentValues values = new ContentValues();
		values.put(StationTableFields.starred.toString(), star ? 1 : 0);
		
		return values;
	}
	
	/**
	 * Unstar all stations.
	 */
	public void unstarAll() {
		ContentValues values = new ContentValues();
		values.put(StationTableFields.starred.toString(), 0);
		db.update(StationTable.TABLE_NAME, values, null, null);
	}
	
	/**
	 * Update infos from a detailled sstation.
	 */
	public void update(Station station) {
		final ContentValues values = station.getUpdatableContentValues();
		updateStation(values, station);
	}
	
	private void updateStation(ContentValues values, Station station) {
		db.update(StationTable.TABLE_NAME, values, StationTableFields._id + "=?", new String[] { station.getId().toString() });
	}
	
	// Suggestions queries...
	
	public static final String STATION_NAME = StationTableFields.suggest_text_1.toString();
	public static final String STATION_ID = StationTableFields._id.toString();
	
	/**
	 * Returns a Cursor positioned at the word specified by rowId
	 * 
	 * @param rowId
	 *            id of word to retrieve
	 * @param columns
	 *            The columns to include, if null then all are included
	 * @return Cursor positioned to matching word, or null if not found.
	 */
	public Cursor getWord(String rowId, String[] columns) {
		String selection = "rowid = ?";
		String[] selectionArgs = new String[] { rowId };

		return query(selection, selectionArgs, columns);

		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table> WHERE rowid = <rowId>
		 */
	}

	/**
	 * Returns a Cursor over all words that match the given query
	 * 
	 * @param query
	 *            The string to search for
	 * @param columns
	 *            The columns to include, if null then all are included
	 * @return Cursor over all words that match, or null if none found.
	 */
	public Cursor getWordMatches(String query, String[] columns) {
		String selection = STATION_NAME + " LIKE ?";
		String[] selectionArgs = new String[] { "%" + query + "%" };

		return query(selection, selectionArgs, columns);

		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table> WHERE <KEY_WORD> LIKE '%query%'.
		 */
	}

	/**
	 * Performs a database query.
	 * 
	 * @param selection
	 *            The selection clause
	 * @param selectionArgs
	 *            Selection arguments for "?" components in the selection
	 * @param columns
	 *            The columns to return
	 * @return A Cursor over all rows matching the query
	 */
	private Cursor query(String selection, String[] selectionArgs, String[] columns) {
		/*
		 * The SQLiteBuilder provides a map for all possible columns requested to actual columns in the database,
		 * creating a simple column alias mechanism by which the ContentProvider does not need to know the real column
		 * names
		 */
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(StationTable.TABLE_NAME);
		builder.setProjectionMap(getColumnMap());

		Cursor cursor = builder.query(VlilleChecker.getDbAdapter().getReadableDatabase(), //
				columns, selection, selectionArgs, //
				null, null, null);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		
		return cursor;
	}	
	
	private HashMap<String, String> getColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(STATION_NAME, STATION_NAME);
		map.put(STATION_ID, STATION_ID);
		map.put(STATION_ID, "rowid AS " + STATION_ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
		
		return map;
	}	
	
	/**
	 * Vlille open helper.
	 * Helps to initialize the sqlite database.
	 */
	class VlilleOpenHelper extends SQLiteOpenHelper {

		public VlilleOpenHelper(Context context) {
			this(context, DbSchema.DB_NAME, null, DbSchema.VERSION);
		}
	
		public VlilleOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(LOG_TAG, "db #onCreate");

			new Thread(new Runnable() {
				public void run() {
					loadStations();
				}
			}).start();			
		}
		
		private void loadStations() {
			final DbSchema vlilleCheckerDb = new DbSchema();
			for (Table eachTable : vlilleCheckerDb.getTables()) {
				Log.d(LOG_TAG, "Create table " + eachTable.getName());
				db.execSQL(eachTable.toString());
			}
			
			final SetStationsInfos setStationsInfos = ContextHelper.parseAllStations(context);
			final StationsMapsInfos mapInfos = setStationsInfos.getMapsInfos();
			final List<Station> stations = setStationsInfos.getStations();
			
			StopWatch watcher = new StopWatch();
			watcher.start();
			
			Log.d(LOG_TAG, "Insert maps infos");
			db.insert(MapInfosTable.TABLE_NAME, null, mapInfos.getInsertableContentValues());
			
			Log.d(LOG_TAG, "Insert all stations infos.");
			for (Station eachStation : stations) {
				db.insert(StationTable.TABLE_NAME, null, eachStation.getInsertableContentValues());
			}
			
			watcher.stop();
			Log.d(LOG_TAG, "Time to initialize db: " + watcher.getTime());
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(LOG_TAG, "db #onUpgrade " + oldVersion + " to " + newVersion);
			db.execSQL("DROP TABLE IF EXISTS " + StationTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + MapInfosTable.TABLE_NAME);
			
			onCreate(db);
		}
		
	}

}

package com.vlille.checker.db;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.StopWatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vlille.checker.R;
import com.vlille.checker.db.metadata.MetadataCursorTransformer;
import com.vlille.checker.db.metadata.MetadataTable;
import com.vlille.checker.db.metadata.MetadataTableFields;
import com.vlille.checker.db.station.StationCursorTransformer;
import com.vlille.checker.db.station.StationTable;
import com.vlille.checker.db.station.StationTableFields;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ToastUtils;
import com.vlille.checker.xml.XMLReader;

/**
 * Adapter with helper methods to query the database.
 */
public class DbAdapter {
	
	private final String TAG = getClass().getSimpleName();

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
	
	// Check for update.
	
	public void deleteStation(Long stationId) {
		if (db.delete(StationTable.TABLE_NAME, "_id = " + stationId, null) == 0) {
			throw new IllegalAccessError();
		}
	}

	public void insertStation(Station station) {
		db.insert(StationTable.TABLE_NAME, null, station.getInsertableContentValues());
	}
	
	public void insertMetadata(Metadata metadata) {
		db.insert(MetadataTable.TABLE_NAME, null,metadata.getInsertableContentValues());
	}
	
	public void setLastUpdateTimeToNow() {
		changeLastUpdate(System.currentTimeMillis());
	}
	
	private void changeLastUpdate(long timeInMillis) {
		ContentValues values = new ContentValues();
		values.put(MetadataTableFields.lastUpdate.toString(), timeInMillis);
		
		db.update(MetadataTable.TABLE_NAME, values, null, null);
	}
	
	
	// Stations queries.
	
	/**
	 * Retrieve single station.
	 * 
	 * @param id The station id.
	 * @return The station from the db.
	 */
	public Station find(Long id) {
		return search(StationTableFields._id + "=" + id.toString(), null).get(0);
	}
	
	/**
	 * Retrieve all stations.
	 * 
	 * @return The stations from the db.
	 */
	public List<Station> findAll() {
		return search(null, StationTableFields.suggest_text_1.toString());
	}
	
	/**
	 * Search the starred stations.
	 * 
	 * @return The starred stations ordered by name.
	 */
	public List<Station> getStarredStations() {
		return search(StationTableFields.starred + "=1", StationTableFields.suggest_text_1.toString());
	}
	
	private List<Station> search(String where, String order) {
		final Cursor cursor = db.query(StationTable.TABLE_NAME,
				StationTableFields.getProjection(),
				where, null, null, null, order);
		
		return new StationCursorTransformer(cursor).all();
	}
	
	public void star(Station station) {
		star(true, station);
	}
	
	public void unstar(Station station) {
		star(false, station);
	}
	
	/**
	 * Star or unstar one single station.
	 * 
	 * @param star The starred value.
	 * @param stationId The station id.
	 */
	public void star(boolean star, Station station) {
		Log.d(TAG, "station " + station.getName() + " star? " + star);
		updateStation(getStarredValues(star), station);
	}
	
	/**
	 * Get starred values to update.
	 * 
	 * @param star the starred value.
	 * @return the values to update.
	 */
	private ContentValues getStarredValues(boolean star) {
		final ContentValues values = new ContentValues();
		values.put(StationTableFields.starred.toString(), star ? 1 : 0);
		
		return values;
	}
	
	public boolean isStarred(Station station) {
		Log.d(TAG, "#isStarred");
		final Cursor cursor = db.query(StationTable.TABLE_NAME,
					new String[] { StationTableFields.starred.toString() },
					StationTableFields._id + "=" + station.getId(),
					null, null, null, null);
		cursor.moveToFirst();
		
		return BooleanUtils.toBoolean(cursor.getInt(0));
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
		db.update(StationTable.TABLE_NAME, values, StationTableFields._id + "=?", new String[] { station.getId() });
	}
	
	/**
	 * Maps station with metadata query.
	 * 
	 * @return a set of station infos with metadata and all stations.
	 */
	public SetStationsInfos findSetStationsInfos() {
		final List<Station> stations = findAll();
		final Metadata metadata = findMetadata();
		
		return new SetStationsInfos(metadata, stations);
	}
	
	// Metadata queries.
	
	public Metadata findMetadata() {
		final Cursor cursor = db.query(MetadataTable.TABLE_NAME,
				MetadataTableFields.getProjection(), null, null, null, null, null);
		if (cursor == null) {
			return null;
		}
		
		return new MetadataCursorTransformer(cursor).first();
	}
	
	// Helper methods.
	
	public void execSQL(String sql) {
		db.execSQL(sql);
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
		public void onCreate(final SQLiteDatabase database) {
			Log.d(TAG, "db #onCreate");

			db = database;
			loadStations();
		}
		
		public void loadStations() {
			Log.d(TAG, "loadStations to create database.");
			
			final SetStationsInfos setStationsInfos = getSetStations();
	
			StopWatch watcher = new StopWatch();
			watcher.start();
	
			createTables();
			initMetadata(setStationsInfos.getMetadata());
			initStations(setStationsInfos.getStations());
	
			ToastUtils.show(context, R.string.installation_done);
	
			watcher.stop();
			Log.d(TAG, "Time to initialize db: " + watcher.getTime());
		}

		private SetStationsInfos getSetStations() {
			return new XMLReader().getLocalSetStationsInfos(context);
		}
	
		private void createTables() {
			final DbSchema vlilleCheckerDb = new DbSchema();
			for (Table eachTable : vlilleCheckerDb.getTables()) {
				Log.d(TAG, "Create table " + eachTable.getName());
				execSQL(eachTable.toString());
			}
		}
	
		private void initMetadata(final Metadata metadata) {
			Log.d(TAG, "Insert maps infos");
			insertMetadata(metadata);
		}
	
		private void initStations(final List<Station> stations) {
			Log.d(TAG, "Insert all stations infos.");
			for (Station eachStation : stations) {
				insertStation(eachStation);
			}
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "db #onUpgrade " + oldVersion + " to " + newVersion);
			db.execSQL("DROP TABLE IF EXISTS " + StationTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + MetadataTable.TABLE_NAME);
			
			onCreate(db);
		}
		
	}

}

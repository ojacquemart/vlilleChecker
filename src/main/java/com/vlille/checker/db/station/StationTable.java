package com.vlille.checker.db.station;

import static com.vlille.checker.db.station.StationTableFields._id;
import static com.vlille.checker.db.station.StationTableFields.adress;
import static com.vlille.checker.db.station.StationTableFields.attachs;
import static com.vlille.checker.db.station.StationTableFields.bikes;
import static com.vlille.checker.db.station.StationTableFields.cbPaiement;
import static com.vlille.checker.db.station.StationTableFields.lastUpdate;
import static com.vlille.checker.db.station.StationTableFields.latitude;
import static com.vlille.checker.db.station.StationTableFields.latitudeE6;
import static com.vlille.checker.db.station.StationTableFields.longitude;
import static com.vlille.checker.db.station.StationTableFields.longitudeE6;
import static com.vlille.checker.db.station.StationTableFields.ordinal;
import static com.vlille.checker.db.station.StationTableFields.outOfService;
import static com.vlille.checker.db.station.StationTableFields.starred;
import static com.vlille.checker.db.station.StationTableFields.suggest_text_1;

import com.vlille.checker.db.Field;
import com.vlille.checker.db.Table;
import com.vlille.checker.db.Type;

/**
 * The station table.
 */
public class StationTable extends Table {
	
	public static final String TABLE_NAME = "station";
	
	public StationTable() {
		super(TABLE_NAME);
		
		add(Field.newField(_id).type(Type.INTEGER).primaryKey());
		add(Field.newField(suggest_text_1).type(Type.STRING));
		add(Field.newField(latitude).type(Type.REAL));
		add(Field.newField(longitude).type(Type.REAL));
		add(Field.newField(latitudeE6).type(Type.INTEGER));
		add(Field.newField(longitudeE6).type(Type.INTEGER));
		add(Field.newField(adress).type(Type.STRING).nullable());
		add(Field.newField(bikes).type(Type.INTEGER).nullable());
		add(Field.newField(attachs).type(Type.INTEGER).nullable());
		add(Field.newField(cbPaiement).type(Type.INTEGER).nullable());
		add(Field.newField(outOfService).type(Type.INTEGER).nullable());
		add(Field.newField(lastUpdate).type(Type.INTEGER).nullable());
		add(Field.newField(starred).type(Type.INTEGER).nullable());
		add(Field.newField(ordinal).type(Type.INTEGER).nullable());
	}

}

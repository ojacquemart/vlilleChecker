package com.vlille.checker.db;

import static com.vlille.checker.db.StationTableFields.*;

public class StationTable extends Table {
	
	public static final String TABLE_NAME = "station";
	
	public StationTable() {
		super(TABLE_NAME);
		
		add(Field.newField(_id).type(Type.INTEGER).primaryKey());
		add(Field.newField(suggest_text_1).type(Type.STRING));
		add(Field.newField(latitude).type(Type.REAL));
		add(Field.newField(longitude).type(Type.REAL));
		add(Field.newField(latitudeE6).type(Type.REAL));
		add(Field.newField(longitudeE6).type(Type.REAL));
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

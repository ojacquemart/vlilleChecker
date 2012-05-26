package com.vlille.checker.db;

import static com.vlille.checker.db.MapInfosTableFields.*;

public class MapInfosTable extends Table {

	public static final String TABLE_NAME = "map_infos"; 
	
	public MapInfosTable() {
		super(TABLE_NAME);
		
		add(Field.newField(LATITUDE).type(Type.INTEGER).notNullable());
		add(Field.newField(LONGITUTDE).type(Type.INTEGER).notNullable());
	}

}

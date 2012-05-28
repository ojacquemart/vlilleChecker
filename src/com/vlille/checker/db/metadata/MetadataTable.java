package com.vlille.checker.db.metadata;

import static com.vlille.checker.db.metadata.MetadataTableFields.lastUpdate;
import static com.vlille.checker.db.metadata.MetadataTableFields.latitudeE6;
import static com.vlille.checker.db.metadata.MetadataTableFields.longitudeE6;

import com.vlille.checker.db.Field;
import com.vlille.checker.db.Table;
import com.vlille.checker.db.Type;

/**
 * The metada table to store some informations like:
 * <li>the last time when was updated the stations list
 * <li>the latitude given by vlille to center the google maps
 * <li>the longitude given by vlille to center the google maps
 */
public class MetadataTable extends Table {

	public static final String TABLE_NAME = "vlille_metadata"; 
	
	public MetadataTable() {
		super(TABLE_NAME);
		
		add(Field.newField(lastUpdate).type(Type.INTEGER).nullable());
		add(Field.newField(latitudeE6).type(Type.INTEGER).notNullable());
		add(Field.newField(longitudeE6).type(Type.INTEGER).notNullable());
	}

}

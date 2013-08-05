package com.vlille.checker.db.metadata;

import android.database.Cursor;

import com.vlille.checker.db.CursorTransformer;
import com.vlille.checker.model.Metadata;

public class MetadataCursorTransformer extends CursorTransformer<Metadata> {

	public MetadataCursorTransformer(Cursor cursor) {
		super(cursor);
	}

	@Override
	public Metadata single() {
		Metadata metadata = new Metadata();
		metadata.setLastUpdate(cursor.getLong(MetadataTableFields.lastUpdate.ordinal()));
		metadata.setLatitude1e6(cursor.getInt(MetadataTableFields.latitudeE6.ordinal()));
		metadata.setLongitude1e6(cursor.getInt(MetadataTableFields.longitudeE6.ordinal()));
		return metadata;
	}

}

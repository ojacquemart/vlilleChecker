package com.vlille.checker.db.station;

import org.apache.commons.lang3.BooleanUtils;

import android.database.Cursor;

import com.vlille.checker.db.CursorTransformer;
import com.vlille.checker.model.Station;

public class StationCursorTransformer extends CursorTransformer<Station> {
	
	public StationCursorTransformer(Cursor cursor) {
		super(cursor);
	}

	@Override
	public Station single()  {
		Station station = new Station();
		station.setId(cursor.getString(StationTableFields._id.ordinal()));	
		station.setName(cursor.getString(StationTableFields.suggest_text_1.ordinal()));	
		station.setAdress(cursor.getString(StationTableFields.adress.ordinal()));	
		station.setBikes(cursor.getString(StationTableFields.bikes.ordinal()));	
		station.setAttachs(cursor.getString(StationTableFields.attachs.ordinal()));	
		station.setLatitude(cursor.getDouble(StationTableFields.latitude.ordinal()));	
		station.setLongitude(cursor.getDouble(StationTableFields.longitude.ordinal()));
		station.setLatitudeE6(cursor.getInt(StationTableFields.latitudeE6.ordinal()));
		station.setLongitudeE6(cursor.getInt(StationTableFields.longitudeE6.ordinal()));
		station.setCbPaiement(BooleanUtils.toBoolean(cursor.getInt(StationTableFields.cbPaiement.ordinal())));
		station.setOufOfService(BooleanUtils.toBoolean(cursor.getInt(StationTableFields.outOfService.ordinal())));
		station.setOrdinal(cursor.getInt(StationTableFields.ordinal.ordinal()));
		station.setLastUpdate(cursor.getLong(StationTableFields.lastUpdate.ordinal()));
		station.setStarred(BooleanUtils.toBoolean(cursor.getInt(StationTableFields.starred.ordinal())));
		
		return station;
	}

}

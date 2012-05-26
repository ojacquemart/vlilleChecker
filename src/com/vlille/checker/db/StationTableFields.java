package com.vlille.checker.db;

/**
 * _id and suggest_text_1 are necessary for suggestions.
 * The suggest_text_1 corresponds to the station name.
 * @see http://developer.android.com/guide/topics/search/adding-custom-suggestions.html#SuggestionTable
 */
public enum StationTableFields {

	_id,
	suggest_text_1,
	latitude,
	longitude,
	latitudeE6,
	longitudeE6,
	adress,
	bikes,
	attachs,
	cbPaiement,
	outOfService,
	lastUpdate,
	starred,
	ordinal
	;
	
	public static String[] getProjection() {
		final StationTableFields[] values = values();
		String[] fields = new String[values.length];
		
		for (int i = 0; i < values.length; i++) {
			fields[i] = values[i].toString();
		}
		
		return fields;
	}
	
}

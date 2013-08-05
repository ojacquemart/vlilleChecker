package com.vlille.checker.db.metadata;

import com.vlille.checker.db.ProjectionUtils;


public enum MetadataTableFields {

	lastUpdate,
	latitudeE6,
	longitudeE6,
	;
	
	public static String[] getProjection() {
		return ProjectionUtils.generateProjectionFields(values());
	}
	
}

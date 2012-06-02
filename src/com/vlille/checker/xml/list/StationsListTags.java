package com.vlille.checker.xml.list;

public enum StationsListTags {

	MARKERS("markers"),
	CENTER_LATITUDE("center_lat"),
	CENTER_LONGITUDE("center_lng"),
	ZOOM_LEVEL("zoom_level"),
	
	MARKER("marker"),
	ID("id"),
	NAME("name"),
	LATITUDE("lat"),
	LONGITUDE("lng");
	
	private String name;
	
	private StationsListTags(String name) {
		this.name = name;
	}

	public final String tag() {
		return name;
	}
	
}

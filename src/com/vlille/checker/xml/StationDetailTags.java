package com.vlille.checker.xml;

public enum StationDetailTags {
	STATION("station"),
	
	ADRESS("adress"),
	STATUS("status"),
	BIKES("bikes"),
	ATTACHS("attachs"),
	PAIEMENT("paiement"),
	LAST_UPDATE("lastupd");
	
	private String name;
	
	private StationDetailTags(String name) {
		this.name = name;
	}

	public final String tag() {
		return name;
	}
}

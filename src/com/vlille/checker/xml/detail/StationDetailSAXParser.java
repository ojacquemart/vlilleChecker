package com.vlille.checker.xml.detail;

import com.vlille.checker.model.Station;
import com.vlille.checker.xml.BaseSAXParser;

public class StationDetailSAXParser extends BaseSAXParser<Station> {

	public StationDetailSAXParser(Station station) {
		super(new StationDetailHandler(station));
	}

}

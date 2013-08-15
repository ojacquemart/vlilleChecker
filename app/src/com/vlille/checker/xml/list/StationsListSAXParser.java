package com.vlille.checker.xml.list;

import com.vlille.checker.model.SetStationsInfo;
import com.vlille.checker.xml.BaseSAXParser;

public class StationsListSAXParser extends BaseSAXParser<SetStationsInfo> {

	public StationsListSAXParser() {
		super(new StationsListHandler());
	}

}

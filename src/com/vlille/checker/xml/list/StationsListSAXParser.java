package com.vlille.checker.xml.list;

import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.xml.BaseSAXParser;

public class StationsListSAXParser extends BaseSAXParser<SetStationsInfos> {

	public StationsListSAXParser() {
		super(new StationsListHandler());
	}

}

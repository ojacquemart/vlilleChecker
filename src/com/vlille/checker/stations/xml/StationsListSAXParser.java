package com.vlille.checker.stations.xml;

import java.io.InputStream;

import com.vlille.checker.model.SetStationsInfos;

public class StationsListSAXParser extends BaseSAXParser implements
		StationsListParser {

	public StationsListSAXParser(InputStream inputStream) {
		super(inputStream);
	}

	@Override
	public SetStationsInfos parse() {
		try {
			StationsListHandler handler = new StationsListHandler();
			doParse(handler);
			
			return handler.getStationSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

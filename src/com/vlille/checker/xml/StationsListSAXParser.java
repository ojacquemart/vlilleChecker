package com.vlille.checker.xml;

import java.io.InputStream;

import com.vlille.checker.model.StationSet;

public class StationsListSAXParser extends BaseSAXParser implements
		StationsListParser {

	public StationsListSAXParser(InputStream inputStream) {
		super(inputStream);
	}

	@Override
	public StationSet parse() {
		try {
			StationsListHandler handler = new StationsListHandler();
			doParse(handler);
			
			return handler.getStationSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

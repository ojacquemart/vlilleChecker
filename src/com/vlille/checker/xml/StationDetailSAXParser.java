package com.vlille.checker.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.vlille.checker.model.Station;

public class StationDetailSAXParser extends BaseSAXParser implements StationDetailParser {

	public StationDetailSAXParser(InputStream inputStream) {
		super(inputStream);
	}

	@Override
	public Station parse() throws SAXException, IOException, ParserConfigurationException {
		StationDetailHandler handler = new StationDetailHandler();
		doParse(handler);
		
		return handler.getStation();
	}

}

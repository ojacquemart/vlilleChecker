package com.vlille.checker.stations.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;


public class BaseSAXParser {

	protected InputStream inputStream;

	public BaseSAXParser(InputStream inputStream) {
		try {
			this.inputStream = new UnicodeBOMInputStream(inputStream).skipBOM();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	protected void doParse(BaseStationHandler handler) throws SAXException, IOException, ParserConfigurationException {
		getSaxParser().parse(inputStream, handler);
	}
	
	protected SAXParser getSaxParser() throws ParserConfigurationException, SAXException {
		return SAXParserFactory.newInstance().newSAXParser();
	}

}

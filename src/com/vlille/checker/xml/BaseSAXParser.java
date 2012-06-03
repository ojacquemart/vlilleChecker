package com.vlille.checker.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;


public abstract class BaseSAXParser<T> {
	
	private BaseStationHandler<T> handler;
	
	public BaseSAXParser(BaseStationHandler<T> handler) {
		this.handler = handler;
	}

	public T parse(InputStream inputStream) {
		try {
			doParse(new UnicodeBOMInputStream(inputStream).skipBOM());
			
			return handler.getResult();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void doParse(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
		getSaxParser().parse(inputStream, handler);
	}
	
	private SAXParser getSaxParser() throws ParserConfigurationException, SAXException {
		return SAXParserFactory.newInstance().newSAXParser();
	}

}

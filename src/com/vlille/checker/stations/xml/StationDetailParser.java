package com.vlille.checker.stations.xml;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.vlille.checker.model.Station;

public interface StationDetailParser {

	Station parse() throws SAXException, IOException, ParserConfigurationException;
}

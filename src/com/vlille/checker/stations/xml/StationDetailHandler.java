package com.vlille.checker.stations.xml;

import static com.vlille.checker.stations.xml.StationDetailTags.ADRESS;
import static com.vlille.checker.stations.xml.StationDetailTags.ATTACHS;
import static com.vlille.checker.stations.xml.StationDetailTags.BIKES;
import static com.vlille.checker.stations.xml.StationDetailTags.LAST_UPDATE;
import static com.vlille.checker.stations.xml.StationDetailTags.PAIEMENT;
import static com.vlille.checker.stations.xml.StationDetailTags.STATUS;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.vlille.checker.model.Station;

public class StationDetailHandler extends BaseStationHandler {

	private Station station;
	private StringBuilder builder = new StringBuilder();

	public Station getStation() {
		return station;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (this.station != null) {
			String data = builder.toString().trim();
			if (localName.equalsIgnoreCase(ADRESS.tag())) {
				station.setAdress(data);
			} else if (localName.equalsIgnoreCase(STATUS.tag())) {
				station.setStatus(data);
			} else if (localName.equalsIgnoreCase(BIKES.tag())) {
				station.setBikes(data);
			} else if (localName.equalsIgnoreCase(ATTACHS.tag())) {
				station.setAttachs(data);
			} else if (localName.equalsIgnoreCase(PAIEMENT.tag())) {
				station.setPaiement(data);
			} else if (localName.equalsIgnoreCase(LAST_UPDATE.tag())) {
				station.setLastUpdated(data);
			}
			
			builder.setLength(0);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (localName.equalsIgnoreCase(StationDetailTags.STATION.tag())) {
			this.station = new Station();
		}
	}

}

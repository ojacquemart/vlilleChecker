package com.vlille.checker.stations.xml;

import static com.vlille.checker.stations.xml.StationDetailTags.ADRESS;
import static com.vlille.checker.stations.xml.StationDetailTags.ATTACHS;
import static com.vlille.checker.stations.xml.StationDetailTags.BIKES;
import static com.vlille.checker.stations.xml.StationDetailTags.LAST_UPDATE;
import static com.vlille.checker.stations.xml.StationDetailTags.PAIEMENT;
import static com.vlille.checker.stations.xml.StationDetailTags.STATUS;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.vlille.checker.model.Station;
import com.vlille.checker.stations.Constants;

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
				station.setOufOfService(Constants.FLAG_OUT_OF_SERVICE.equals(data));
			} else if (localName.equalsIgnoreCase(BIKES.tag())) {
				station.setBikes(data);
			} else if (localName.equalsIgnoreCase(ATTACHS.tag())) {
				station.setAttachs(data);
			} else if (localName.equalsIgnoreCase(PAIEMENT.tag())) {
				station.setCbPaiement(Constants.FLAG_ALLOWS_CB.equals(data));
			} else if (localName.equalsIgnoreCase(LAST_UPDATE.tag())) {
				long lastUpdate = 0;
				if (!StringUtils.isEmpty(data)) {
					final Long valueOfLastUpdated = Long.valueOf(data.replaceAll("[^\\d]", "").trim());
					if (valueOfLastUpdated != null) {
						lastUpdate = System.currentTimeMillis() + valueOfLastUpdated;
					}
				}
				station.setLastUpdate(lastUpdate);
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

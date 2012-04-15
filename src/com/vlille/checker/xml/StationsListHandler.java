package com.vlille.checker.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.vlille.checker.model.Station;
import com.vlille.checker.model.StationSet;
import com.vlille.checker.model.StationsMapsInformation;
import com.vlille.checker.utils.MiscUtils;

public class StationsListHandler extends BaseStationHandler {

	private StationsMapsInformation mapsInformation = new StationsMapsInformation();
	private List<Station> stations = new ArrayList<Station>();

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		if (localName.equalsIgnoreCase(StationsListTags.MARKERS.tag())) {
			mapsInformation.setLatitude1e6(MiscUtils.transformTo1e6(valueOf(attributes, StationsListTags.CENTER_LATITUDE)));
			mapsInformation.setLongitude1e6(MiscUtils.transformTo1e6(valueOf(attributes, StationsListTags.CENTER_LONGITUDE)));

			String zoom = valueOf(attributes, StationsListTags.ZOOM_LEVEL);
			mapsInformation.setZoom(StringUtils.isEmpty(zoom) ? 0 : Integer.valueOf(zoom));
		}
		if (localName.equalsIgnoreCase(StationsListTags.MARKER.tag())) {
			Station station = new Station();
			station.setId(valueOf(attributes, StationsListTags.ID));
			station.setName(valueOf(attributes, StationsListTags.NAME));
			station.setLatitude1e6(MiscUtils.transformTo1e6(valueOf(attributes, StationsListTags.LATITUDE)));
			station.setLongitute1e6(MiscUtils.transformTo1e6(valueOf(attributes, StationsListTags.LONGITUDE)));

			stations.add(station);
		}
	}

	private String valueOf(Attributes attributes, StationsListTags key) {
		return attributes.getValue(key.tag());
	}

	@Override
	public void endDocument() throws SAXException {
		Collections.sort(stations);
	}

	public StationSet getStationSet() {
		return new StationSet(mapsInformation, stations);
	}

}
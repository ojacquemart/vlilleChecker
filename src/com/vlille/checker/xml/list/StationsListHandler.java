package com.vlille.checker.xml.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.vlille.checker.maps.PositionTransformer;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.SetStationsInfos;
import com.vlille.checker.model.Station;
import com.vlille.checker.xml.BaseStationHandler;

public class StationsListHandler extends BaseStationHandler<SetStationsInfos> {

	private Metadata metadata = new Metadata();
	private Set<Station> stations = new HashSet<Station>();

	@Override
	public SetStationsInfos getResult() {
		return new SetStationsInfos(metadata, new ArrayList<Station>(stations));
	}
	
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		if (localName.equalsIgnoreCase(StationsListTags.MARKERS.tag())) {
			metadata.setLatitude1e6(PositionTransformer.to1e6(valueOf(attributes, StationsListTags.CENTER_LATITUDE)));
			metadata.setLongitude1e6(PositionTransformer.to1e6(valueOf(attributes, StationsListTags.CENTER_LONGITUDE)));
		}
		if (localName.equalsIgnoreCase(StationsListTags.MARKER.tag())) {
			Station station = new Station();
			station.setId(valueOf(attributes, StationsListTags.ID));
			station.setName(valueOf(attributes, StationsListTags.NAME));
			final String valueLatitude = valueOf(attributes, StationsListTags.LATITUDE);
			station.setLatitudeE6(PositionTransformer.to1e6(valueLatitude));
			station.setLatitude(Double.valueOf(valueLatitude));
			final String valueLongitude = valueOf(attributes, StationsListTags.LONGITUDE);
			station.setLongitude(Double.valueOf(valueLongitude));
			station.setLongitudeE6(PositionTransformer.to1e6(valueLongitude));

			stations.add(station);
		}
	}

	private String valueOf(Attributes attributes, StationsListTags key) {
		return attributes.getValue(key.tag());
	}

}

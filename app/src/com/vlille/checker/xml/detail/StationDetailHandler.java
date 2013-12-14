package com.vlille.checker.xml.detail;

import android.text.TextUtils;

import com.vlille.checker.model.Station;
import com.vlille.checker.xml.BaseStationHandler;

import org.xml.sax.SAXException;

import static com.vlille.checker.xml.detail.StationDetailTags.ADRESS;
import static com.vlille.checker.xml.detail.StationDetailTags.ATTACHS;
import static com.vlille.checker.xml.detail.StationDetailTags.BIKES;
import static com.vlille.checker.xml.detail.StationDetailTags.LAST_UPDATE;
import static com.vlille.checker.xml.detail.StationDetailTags.PAIEMENT;
import static com.vlille.checker.xml.detail.StationDetailTags.STATUS;

public class StationDetailHandler extends BaseStationHandler<Station> {

    /**
     * Marker for station allowing payment by credit card.
     */
    private static final String FLAG_ALLOWS_CB = "AVEC_TPE";

    /**
     * Marker for station out of service = 1, available = 0.
     */
    private static final String FLAG_OUT_OF_SERVICE = "1";

	private Station station;
	private StringBuilder builder = new StringBuilder();

	public StationDetailHandler(Station station) {
		this.station = station;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		String data = builder.toString().trim();
		if (localName.equalsIgnoreCase(ADRESS.tag())) {
			station.setAdress(data);
		} else if (localName.equalsIgnoreCase(STATUS.tag())) {
			station.setOufOfService(FLAG_OUT_OF_SERVICE.equals(data));
		} else if (localName.equalsIgnoreCase(BIKES.tag())) {
			station.setBikes(data);
		} else if (localName.equalsIgnoreCase(ATTACHS.tag())) {
			station.setAttachs(data);
		} else if (localName.equalsIgnoreCase(PAIEMENT.tag())) {
			station.setCbPaiement(FLAG_ALLOWS_CB.equals(data));
		} else if (localName.equalsIgnoreCase(LAST_UPDATE.tag())) {
			long lastUpdate = 0;
            if (!TextUtils.isEmpty(data)) {
				final Long valueOfLastUpdated = Long.valueOf(data.replaceAll("[^\\d]", "").trim());
				if (valueOfLastUpdated != null) {
					lastUpdate = valueOfLastUpdated;
				}
			}

			station.setLastUpdate(lastUpdate);
		}

		builder.setLength(0);
	}

	@Override
	public Station getResult() {
		return station;
	}

}

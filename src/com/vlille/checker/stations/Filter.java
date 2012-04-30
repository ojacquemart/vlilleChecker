package com.vlille.checker.stations;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vlille.checker.model.Station;

/**
 * Search for a station name among the stations list.
 */
public class Filter {

	public static List<Station> doFilter(List<Station> stations, String keyword) {
		if (keyword == null || keyword.length() == 0) {
			return stations;
		}
		
		List<Station> result = new ArrayList<Station>();
		keyword = StringUtils.stripAccents(keyword).toLowerCase();

		for (Station eachStation : stations) {
			if (eachStation.getName().toLowerCase().contains(keyword)) {
				result.add(eachStation);
			}
		}

		return result;
	}
	
}

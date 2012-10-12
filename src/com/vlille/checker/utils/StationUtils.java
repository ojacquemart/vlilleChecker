package com.vlille.checker.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vlille.checker.model.Station;

/**
 * Search for a station name among the stations list.
 */
public class StationUtils {

	/**
	 * Convert a list of stations to map indexed by id station.
	 * @param stations full list of stations.
	 * @return map for easier search into list.
	 */
	public static Map<String, Station> toMap(List<Station> stations) {
		Map<String, Station> map = new HashMap<String, Station>();
		
		for (Station eachStation : stations) {
			map.put(eachStation.getId(), eachStation);
		}
		
		return map;
	}
	
	public static List<Station> filter(List<Station> stations, String keyword) {
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

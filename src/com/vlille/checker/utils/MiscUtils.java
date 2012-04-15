package com.vlille.checker.utils;

import java.util.HashMap;
import java.util.List;
import com.vlille.checker.model.Station;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MiscUtils {

	public static void showOrMask(ViewGroup layout, boolean show) {
		if (layout == null) {
			return;
		}
		
		layout.setVisibility(show ? LinearLayout.VISIBLE : LinearLayout.GONE);
	}
	
	/**
	 * Camelized a string.
	 * @param text to camelize.
	 * @return text camelized.
	 */
	public static String toCamelCase(String text) {
		if (text == null || text.length() == 0) {
			return "";
		}
		
		if (text.length() == 1) {
			return text;
		}
		
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}
	
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
	
	/**
	 * Transform string value of xml value in google aps longitude or latitude.
	 * @param value
	 * @return
	 */
	public static int transformTo1e6(String value) {
		if (StringUtils.isEmpty(value)) {
			return 0;
		}
			
		return (int) (Double.valueOf(value) * 1e6);
	}
	
}

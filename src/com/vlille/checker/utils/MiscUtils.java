package com.vlille.checker.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vlille.checker.model.Station;

public class MiscUtils {

	public static void showOrMask(ViewGroup layout, boolean show) {
		if (layout == null) {
			return;
		}
		
		layout.setVisibility(show ? View.VISIBLE : View.GONE);
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
	
}

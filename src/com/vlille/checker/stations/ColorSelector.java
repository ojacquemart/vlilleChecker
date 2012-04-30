package com.vlille.checker.stations;

import com.vlille.checker.R;

public class ColorSelector {
	
	private static final int NB_RED = 0;
	private static final int NB_MIN_ORANGE = 1;
	private static final int NB_MAX_ORANGE = 5;
	
	// The default color from list is black.
	private static int DEFAULT_LIST_COLOR = R.color.black;
	// The default color from maps is white, more readable.
	private static int DEFAULT_MAPS_COLOR = R.color.white;

	/**
	 * Get color from a given number of bikes.
	 * @param number the bike number.
	 * @param mapsDisplay display is in maps.
	 * @return the resource color.
	 * <li>0 bike: red color
	 * <li>between 1 and 5: orange
	 * <li>above 5 and mapsDisplay is true: white, otherwise: black.
	 */
	public static int getColor(int number, boolean mapsDisplay) {
		if (number == NB_RED) {
			return R.color.red;
		}
		if (number >= NB_MIN_ORANGE && number <= NB_MAX_ORANGE) {
			return R.color.orange;
		}
		if (mapsDisplay) {
			return DEFAULT_MAPS_COLOR;
		}
		
		return DEFAULT_LIST_COLOR;
	}
	
	public static int getColor(int number) {
		return getColor(number, true);
	}
	
}

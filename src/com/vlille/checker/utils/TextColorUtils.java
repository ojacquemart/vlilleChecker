package com.vlille.checker.utils;

import android.graphics.Color;

import com.vlille.checker.R;

/**
 * Utils class to display the number of bikes or attachs
 * remaining in differents colors according to the number.
 */
public class TextColorUtils {
	
	private static final int NB_RED = 0;
	private static final int NB_MIN_ORANGE = 1;
	private static final int NB_MAX_ORANGE = 5;
	
	private static final String COLOR_HEXA_WHITE = "#FFFFFF";
	private static final String COLOR_HEXA_RED = "#FF0000";
	private static final String COLOR_HEXA_ORANGE = "#FF9100";

	public static int getColorFromResources(int number) {
		int color = R.color.black;
		
		if (number == NB_RED) {
			color = R.color.red;
		}
		else if (number >= NB_MIN_ORANGE && number <= NB_MAX_ORANGE) {
			color = R.color.orange;
		}
		
		return color;
	}
	
	public static int getColorFromHexa(int number) {
		int color = Color.parseColor(COLOR_HEXA_WHITE);
		
		if (number == NB_RED) {
			color = Color.parseColor(COLOR_HEXA_RED);
		}
		else if (number > NB_RED && number <= NB_MIN_ORANGE) {
			color = Color.parseColor(COLOR_HEXA_ORANGE);
		}
		
		return color;
	}
	
}

package com.vlille.checker.utils;

public final class TextUtils {
	
	private TextUtils() {}
	
	public static String toCamelCase(String text) {
		if (text == null || text.length() == 0) {
			return "";
		}
		
		if (text.length() == 1) {
			return text;
		}
		
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}
	
	
	
}

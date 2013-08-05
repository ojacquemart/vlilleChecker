package com.vlille.checker.utils;

import org.apache.commons.lang3.text.WordUtils;

public  final class TextUtils {
	
	private TextUtils() {}
	
	public static String toCamelCase(String text) {
		return WordUtils.capitalize(text);
		/*if (text == null || text.length() == 0) {
			return "";
		}
		
		if (text.length() == 1) {
			return text;
		}
		
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
		*/
	}
	
	/**
	 * Add a "s" to a text to format according to a given value.
	 * <pre>
	 * formatPlural(0, "my %d cent") = my 0 cent
	 * formatPlural(1, "my %d cent") = my 1 cent
	 * formatPlural(2, "my %d cent") = my 2 cents
	 * </pre>
	 * 
	 * @param value the value to format in text
	 * @param text the text to format with value.
	 * 
	 * @return the text in plural form.
	 */
	public static String formatPlural(long value, String text) {
		text = String.format(text, value);
		if (value > 1) {
			text += "s";
		}
		
		return text;
	}
	
}

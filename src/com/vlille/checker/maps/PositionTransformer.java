package com.vlille.checker.maps;

import org.apache.commons.lang3.StringUtils;

public class PositionTransformer {

	public static final double VALUE_1E6 = 1e6;

	/**
	 * Transform string value of xml value in google aps longitude or latitude.
	 * @param value
	 * @return
	 */
	public static int to1e6(String value) {
		if (StringUtils.isEmpty(value)) {
			return 0;
		}
			
		return (int) (Double.valueOf(value) * VALUE_1E6);
	}
	
	/**
	 * Transform double position value in gmaps value.
	 * @param value the value.
	 * @return the gmaps value.
	 */
	public static int toE6(double value) {
		return (int) (value * VALUE_1E6);
	}
	
	public static double toNormal(int e6Value) {
		return e6Value / VALUE_1E6;
	}
	
}

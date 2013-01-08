package com.vlille.checker.db;

public class ProjectionUtils {

	public static <T> String[] generateProjectionFields(T[] values) {
		String[] fields = new String[values.length];
		
		for (int i = 0; i < values.length; i++) {
			fields[i] = values[i].toString();
		}
		
		return fields;
	}
	
}

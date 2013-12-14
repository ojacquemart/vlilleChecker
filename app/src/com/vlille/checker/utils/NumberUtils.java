package com.vlille.checker.utils;

public final class NumberUtils {

    private NumberUtils() {}

    public static final int INTEGER_ZERO = 0;

    /**
     * Convert a String to an int, returning a default value if the conversion fails.
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static int toInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
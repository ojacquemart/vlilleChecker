package com.vlille.checker.utils.color;

public final class ColorSelector {

    private static final ColorConfiguration LIST_COLOR_CONFIGURATION = new ListColorConfiguration();
    private static final ColorConfiguration MAP_COLOR_CONFIGURATION = new MapColorConfiguration();

    private ColorSelector() {
    }

    public static int getColor(int number) {
        return LIST_COLOR_CONFIGURATION.getColor(number);
    }

    public static int getColorForMap(int number) {
        return MAP_COLOR_CONFIGURATION.getColor(number);
    }

}

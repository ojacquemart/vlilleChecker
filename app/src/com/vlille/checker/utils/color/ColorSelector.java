package com.vlille.checker.utils.color;

import android.content.Context;
import android.support.v4.content.ContextCompat;

public final class ColorSelector {

    private static final ColorConfiguration LIST_COLOR_CONFIGURATION = new ListColorConfiguration();
    private static final ColorConfiguration MAP_COLOR_CONFIGURATION = new MapColorConfiguration();

    private ColorSelector() {
    }

    public static int getColor(Context context, int number) {
        return ContextCompat.getColor(context, getColor(number));
    }

    private static int getColor(int number) {
        return LIST_COLOR_CONFIGURATION.getColor(number);
    }

    public static int getColorForMap(Context context, int number) {
        return ContextCompat.getColor(context, getColorForMap(number));
    }

    public static int getColorForMap(int number) {
        return MAP_COLOR_CONFIGURATION.getColor(number);
    }

}

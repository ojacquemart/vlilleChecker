package com.vlille.checker.utils.color;

import com.vlille.checker.R;

abstract class ColorConfiguration {

    private static final int EMPTY = 0;
    private static final int ALMOST_EMPTY_MIN = 1;
    private static final int ALMOST_EMPTY_MAX = 5;

    private static final int ALMOST_EMPTY_COLOR = R.color.orange;

    abstract int getEmptyColor();

    private boolean isAlmostEmpty(int number) {
        return number >= ALMOST_EMPTY_MIN && number <= ALMOST_EMPTY_MAX;
    }

    abstract int getNonEmptyColor();

    /**
     * Gets the color from a given number of bikes.
     *
     * @param number the bike number.
     * @return the resource color.
     * <li>0 bike: EMPTY
     * <li>between 1 and 5: ALMOST_EMPTY
     * <li>above 5 : NON_EMPTY
     */
    public int getColor(int number) {
        if (number == EMPTY) {
            return getEmptyColor();
        }
        if (isAlmostEmpty(number)) {
            return ALMOST_EMPTY_COLOR;
        }

        return getNonEmptyColor();
    }

}
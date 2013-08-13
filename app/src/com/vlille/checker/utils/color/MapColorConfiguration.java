package com.vlille.checker.utils.color;

import com.vlille.checker.R;

/**
 * The map color configuration.
 *
 * The details icon has a red background, black is the empty color and white the non empty.
 */
class MapColorConfiguration extends ColorConfiguration {

    @Override
    int getEmptyColor() {
        return R.color.black;
    }

    @Override
    int getNonEmptyColor() {
        return R.color.white;
    }
}

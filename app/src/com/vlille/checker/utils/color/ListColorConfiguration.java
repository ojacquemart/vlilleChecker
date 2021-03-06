package com.vlille.checker.utils.color;

import com.vlille.checker.R;

/**
 * The list color configuration.
 *
 * Lists have a white background, red is the empty color and black the non empty.
 */
class ListColorConfiguration extends ColorConfiguration {

    @Override
    protected int getEmptyColor() {
        return R.color.red;
    }

    @Override
    protected int getNonEmptyColor() {
        return R.color.black;
    }
}
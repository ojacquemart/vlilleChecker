package com.vlille.checker.model;

import com.vlille.checker.R;

public enum LastUpdateDisplayType {
    NORMAL(R.string.update_ago),
    SHORT(R.string.update_ago_short),
    LONG_AGO(R.string.update_too_long_ago),
    LONG_AGO_SHORT(R.string.update_too_long_ago_short)
    ;

    private int resourceId;

    LastUpdateDisplayType(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}

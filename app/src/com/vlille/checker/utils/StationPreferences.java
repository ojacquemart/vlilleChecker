package com.vlille.checker.utils;

public class StationPreferences {

    private final boolean idVisible;
    private final boolean updatedAtVisible;
    private final boolean addressVisible;

    public StationPreferences(boolean idVisible, boolean updatedAtVisible, boolean addressVisible) {
        this.idVisible = idVisible;
        this.updatedAtVisible = updatedAtVisible;
        this.addressVisible = addressVisible;
    }

    public boolean isIdVisible() {
        return idVisible;
    }

    public boolean isUpdatedAtVisible() {
        return updatedAtVisible;
    }

    public boolean isAddressVisible() {
        return addressVisible;
    }

}

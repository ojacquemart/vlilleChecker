package com.vlille.checker.ui.osm.overlay;

public final class OverlayZoomUtils {

    public static final int MIN_ZOOM_LEVEL_TO_DETAILS = 15;

    private OverlayZoomUtils() {}

    public static boolean isDetailledZoomLevel(int zoomLevel) {
        return zoomLevel > MIN_ZOOM_LEVEL_TO_DETAILS;
    }
}
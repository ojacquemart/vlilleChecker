package com.vlille.checker.ui;

public final class IntentCommunication {

    private IntentCommunication() {}

    /**
     * Code use when storing data into an intent
     */
    public static final String STATION_DATA = "station";

    /**
     * No operation code
     */
    public static final int NO_OP_RESULT_CODE = 0;

    /**
     * Request code when requesting to view station info
     */
    public static final int STATION_INFO_REQUEST_CODE = 100;

    /**
     * Result code when requesting to map station info
     */
    public static final int MAP_GEO_POINT_RESULT_CODE = 200;

    /**
     * Result code when requesting to stations list
     */
    public static final int STATION_STAR_RESULT_CODE = 201;

    public static class Result {

        public static boolean shouldMoveMapToStation(int resultCode) {
            return MAP_GEO_POINT_RESULT_CODE == resultCode;
        }

        public static boolean shouldStarStation(int resultCode) {
            return STATION_STAR_RESULT_CODE == resultCode;
        }

    }
}

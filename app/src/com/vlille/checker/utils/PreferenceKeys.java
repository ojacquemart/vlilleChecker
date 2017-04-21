package com.vlille.checker.utils;

public final class PreferenceKeys {

    /**
     * To display the station id.
     */
    public static final String STATION_ID_VISIBLE = "prefs_station_id_visible";

    /**
     * Shows the station id, by default.
     */
    public static final boolean STATION_ID_VISIBLE_DEFAULT_VALUE = false;

    /**
     * To display the station address in list.
     */
    public static final String STATION_ADDRESS_VISIBLE = "prefs_home_display_adress_value";

    /**
     * Hides the station address, by default.
     */
    public static final boolean STATION_ADDRESS_VISIBLE_DEFAULT_VALUE = false;

    /**
     * To display the last update moment in list.
     */
    public static final String STATION_UPDATED_AT_VISIBLE = "prefs_station_last_update_visible";

    /**
     * Shows the last update moment, by default.
     */
    public static final boolean STATION_UPDATED_AT_VISIBLE_DEFAULT_VALUE = true;

    /**
     * To activate the gps localisation.
     */
    public static final String LOCALISATION_GPS_ACTIVATED = "prefs_localisation_gps_activated_value";

    /**
     * To change the default distance radius for show stations around current position.
     */
    public static final String POSITION_RADIUS = "prefs_position_radius_value";

    /**
     * The default radius value.
     */
    public static final long POSITION_RADIUS_DEFAULT_VALUE = 500L;

    /**
     * The data status last update.
     */
    public static final String DATA_STATUS_LAST_UPDATE = "data_status_last_update";

    /**
     * The current version number.
     */
    public static final String ABOUT_VERSION = "about_version";

    private PreferenceKeys() {}

}

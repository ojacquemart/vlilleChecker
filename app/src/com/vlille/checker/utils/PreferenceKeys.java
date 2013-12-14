package com.vlille.checker.utils;

public final class PreferenceKeys {
	
	private PreferenceKeys() {}

	/**
	 * To display the detailled adress in home screen.
	 */
	public static final String HOME_DISPLAY_ADRESS = "prefs_home_display_adress_value";
	
	/**
	 * The default display station address value.
	 */
	public static final boolean HOME_DISPLAY_DEFAULT_VALUE = false;
	
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

	
}

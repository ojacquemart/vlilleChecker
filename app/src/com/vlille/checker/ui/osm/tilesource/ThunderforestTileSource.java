package com.vlille.checker.ui.osm.tilesource;

import android.content.Context;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.util.ManifestUtil;

/**
 * Thunderforest Maps including OpenCycleMap
 *
 * @see <a href="https://github.com/osmdroid/osmdroid/issues/573">OpenCycleMap now requires API key</a>
 * TODO: remove this class when this tile source will be in a released version.
 */
public class ThunderforestTileSource extends OnlineTileSourceBase {
    /**
     * the meta data key in the manifest
     */
    //<meta-data android:name="THUNDERFOREST_MAPID" android:value="YOUR KEY" />

    private static final String THUNDERFOREST_MAPID = "THUNDERFOREST_MAPID";

    /**
     * the available map types
     */
    public static final int CYCLE = 0;
    public static final int TRANSPORT = 1;
    public static final int LANDSCAPE = 2;
    public static final int OUTDOORS = 3;
    public static final int TRANSPORT_DARK = 4;
    public static final int SPINAL_MAP = 5;
    public static final int PIONEER = 6;
    public static final int MOBILE_ATLAS = 7;
    public static final int NEIGHBOURHOOD = 8;

    //** map names used in URLs */
    private static final String[] urlMap = new String[]{
            "cycle",
            "transport",
            "landscape",
            "outdoors",
            "transport-dark",
            "spinal-map",
            "pioneer",
            "mobile-atlas",
            "neighbourhood"};

    //** map names used in UI (eg. menu) */
    private static final String[] uiMap = new String[]{
            "CycleMap",
            "Transport",
            "Landscape",
            "Outdoors",
            "TransportDark",
            "Spinal",
            "Pioneer",
            "MobileAtlas",
            "Neighbourhood"};

    private static final String[] baseUrl = new String[]{
            "https://a.tile.thunderforest.com/{map}/",
            "https://b.tile.thunderforest.com/{map}/",
            "https://c.tile.thunderforest.com/{map}/"};

    private final int mMap;
    private String mMapId;

    /**
     * creates a new Thunderforest tile source, loading the access token and mapid from the manifest
     */
    public ThunderforestTileSource(final Context ctx,
                                   final int aMap,
                                   final int aZoomMinLevel, final int aZoomMaxLevel,
                                   final int aTileSizePixels) {
        super(uiMap[aMap],
                aZoomMinLevel, aZoomMaxLevel,
                aTileSizePixels,
                ".png", baseUrl,
                "Maps © Thunderforest, Data © OpenStreetMap contributors.");
        mMap = aMap;
        retrieveMapId(ctx);
        //this line will ensure uniqueness in the tile cache
        mName = "thunderforest" + aMap + mMapId;
    }

    /**
     * Reads the map id from the manifest.<br>
     */
    public final void retrieveMapId(final Context aContext) {
        // Retrieve the MapId from the Manifest
        mMapId = ManifestUtil.retrieveKey(aContext, THUNDERFOREST_MAPID);
    }

    @Override
    public String getTileURLString(final MapTile aMapTile) {
        StringBuilder url = new StringBuilder(getBaseUrl().replace("{map}", urlMap[mMap]));
        url.append(aMapTile.getZoomLevel());
        url.append("/");
        url.append(aMapTile.getX());
        url.append("/");
        url.append(aMapTile.getY());
        url.append(".png?");
        url.append("apikey=").append(mMapId);
        String res = url.toString();
        //Log.d(IMapView.LOGTAG, res);

        return res;
    }

    /**
     * check if we have a key in the manifest for this provider.
     *
     * @param aContext
     */
    public static boolean haveMapId(final Context aContext) {
        return !ManifestUtil.retrieveKey(aContext, THUNDERFOREST_MAPID).equals("");
    }

}

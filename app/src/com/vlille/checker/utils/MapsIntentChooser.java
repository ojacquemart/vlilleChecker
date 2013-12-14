package com.vlille.checker.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.vlille.checker.R;
import com.vlille.checker.model.Station;

/**
 * A maps intent chooser.
 *
 * Its main goal is to display an intent chooser
 * if many possible apps can manage the gmaps urls, like:
 * <ul>
 * <li>Native browser</li>
 * <li>Chrome</li>
 * <li>Google Maps App</li>
 * </ul>
 */
public class MapsIntentChooser {

    private static final String TAG = MapsIntentChooser.class.getSimpleName();
    private static final String GMAPS_URL = "http://maps.google.com/maps?saddr=&daddr=%s";

    private Context context;
    private Station station;

    public MapsIntentChooser(Context context, Station station) {
        this.context = context;
        this.station = station;
    }

    public static void chooseIntent(Context context, Station station) {
        MapsIntentChooser mapsIntentChooser = new MapsIntentChooser(context, station);
        mapsIntentChooser.showIntent();
    }

    private static String replaceCommaByDot(String value) {
        return value.replace(",", ".");
    }

    public void showIntent() {
        Intent intent = getShowableIntent();
        if (intent != null) {
            context.startActivity(intent);
        } else {
            showErrorMessage();
        }
    }

    private Intent getShowableIntent() {
        Intent mapsIntent = getMapsIntent();

        int nbAvailableIntents = getNbAvailableIntentsForMaps(mapsIntent);
        if (nbAvailableIntents == 1) {
            return mapsIntent;
        }

        if (nbAvailableIntents > 1) {
            return Intent.createChooser(mapsIntent, context.getString(R.string.open_with));
        }

        return null;
    }

    private Intent getMapsIntent() {
        return new Intent(Intent.ACTION_VIEW, getLocationUri());
    }

    private Uri getLocationUri() {
        Uri location = Uri.parse(String.format(
                GMAPS_URL,
                getLatitudeAndLongitude()));
        Log.d(TAG, "Uri geo " + location);

        return location;
    }

    private String getLatitudeAndLongitude() {
        return String.format(
                "%s,%s",
                replaceCommaByDot(station.getLatitudeAsString()),
                replaceCommaByDot(station.getLongitudeAsString()));
    }

    private int getNbAvailableIntentsForMaps(Intent mapIntent) {
        try {
            PackageManager pkManager = context.getPackageManager();

            return pkManager.queryIntentActivities(mapIntent, 0).size();
        } catch (Exception e) {
            Log.d(TAG, "Error during looking for gmaps activities", e);
            return 0;
        }
    }

    private void showErrorMessage() {
        Toast.makeText(context, R.string.error_no_gmaps_app_found, Toast.LENGTH_SHORT).show();
    }

}
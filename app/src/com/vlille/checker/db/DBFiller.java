package com.vlille.checker.db;

import android.util.Log;

import com.vlille.checker.R;
import com.vlille.checker.model.SetStationsInfo;
import com.vlille.checker.ui.HomeActivity;

public class DBFiller extends DBAction {

    private static final String TAG = DBFiller.class.getSimpleName();

    private HomeActivity homeActivity;

    public DBFiller(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    public void fillIfDbIsEmpty() {
        if (isDBEmpty()) {
            fill();
        }
    }

    public boolean isDBEmpty() {
        return getStationEntityManager().count() == 0;
    }

    public void fill() {
        Log.d(TAG, "Initialize db data!");
        long start = System.currentTimeMillis();

        final SetStationsInfo assetsStationsInfo = getAssetsStationsInfo();

        getMetadataEntityManager().create(assetsStationsInfo.getMetadata());
        getStationEntityManager().create(assetsStationsInfo.getStations());

        homeActivity.showSnackBarMessage(R.string.installation_done);

        long duration = System.currentTimeMillis() - start;
        Log.d(TAG, "Time to initialize db: " + duration + " ms");
    }

}
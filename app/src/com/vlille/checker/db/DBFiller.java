package com.vlille.checker.db;

import android.util.Log;

import com.vlille.checker.R;
import com.vlille.checker.model.SetStationsInfo;
import com.vlille.checker.ui.HomeActivity;
import com.vlille.checker.ui.async.SetStationsInfoAsyncTask;

public class DBFiller extends DBAction implements SetStationsInfoAsyncTask.SetStationsDelegate {

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
        new SetStationsInfoAsyncTask(this).execute();
    }

    @Override
    public void handleResult(SetStationsInfo setStationsInfo) {
        Log.d(TAG, "Initialize db data!");
        long start = System.currentTimeMillis();

        getMetadataEntityManager().create(setStationsInfo.getMetadata());
        getStationEntityManager().create(setStationsInfo.getStations());

        homeActivity.showSnackBarMessage(R.string.installation_done);

        long duration = System.currentTimeMillis() - start;
        Log.d(TAG, "Time to initialize db: " + duration + " ms");
    }
}
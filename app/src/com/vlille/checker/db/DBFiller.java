package com.vlille.checker.db;

import android.util.Log;

import com.vlille.checker.R;
import com.vlille.checker.model.SetStationsInfo;
import com.vlille.checker.utils.ToastUtils;

public class DBFiller extends DBAction {

    private static final String TAG = DBFiller.class.getSimpleName();

    public static void fillIfDbIsEmpty() {
        DBFiller dbFiller = new DBFiller();
        if (dbFiller.isDBEmpty()) {
            dbFiller.fill();
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

        ToastUtils.show(getContext(), R.string.installation_done);

        long duration = System.currentTimeMillis() - start;
        Log.d(TAG, "Time to initialize db: " + duration + " ms");
    }

}
package com.vlille.checker.ui.async;

import android.os.AsyncTask;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.ui.HomeActivity;
import com.vlille.checker.ui.delegate.StationUpdateDelegate;
import com.vlille.checker.xml.XML;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to retrieve details from a stations list.
 */
public abstract class AbstractStationsAsyncTask extends AsyncTask<List<Station>, Void, List<Station>> {

    private static final String TAG = "AsyncStationTaskUpdater";

    private static final XML XML_READER = new XML();

    private final HomeActivity homeActivity;
    private final StationUpdateDelegate delegate;

    private boolean transpoleUnstableState;

    protected AbstractStationsAsyncTask(HomeActivity homeActivity, StationUpdateDelegate delegate) {
        this.homeActivity = homeActivity;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        transpoleUnstableState = false;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        transpoleUnstableState = false;
    }

    @Override
    protected List<Station> doInBackground(List<Station>... params) {
        Log.d(TAG, "Launch background update...");

        final List<Station> stations = new ArrayList<>(params[0]);
        int countStationsFetchInError = 0;

        for (Station station : stations) {
            if (isCancelled()) {
                Log.d(TAG, "Task has been cancelled.");

                return stations;
            }

            station = XML_READER.getRemoteInfo(station);
            delegate.update(station);

            if (station.isFetchInError()) {
                countStationsFetchInError++;
            }

            publishProgress();
        }

        transpoleUnstableState = countStationsFetchInError == stations.size();

        return stations;
    }

    @Override
    protected void onPostExecute(List<Station> stations) {
        super.onPostExecute(stations);

        if (transpoleUnstableState) {
            homeActivity.showTranspoleUnstableMessage();
        }
    }

}

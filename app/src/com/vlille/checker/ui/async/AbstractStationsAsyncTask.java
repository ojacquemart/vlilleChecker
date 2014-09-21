package com.vlille.checker.ui.async;

import android.os.AsyncTask;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.ui.delegate.StationUpdateDelegate;
import com.vlille.checker.xml.XMLReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to retrieve details from a stations list.
 */
public abstract class AbstractStationsAsyncTask extends AsyncTask<List<Station>, Void, List<Station>> {

    private static final String TAG = "AsyncStationTaskUpdater";

    private static final XMLReader XML_READER = new XMLReader();

    private final StationUpdateDelegate delegate;

    protected AbstractStationsAsyncTask(StationUpdateDelegate delegate) {
        if (delegate == null) {
            throw new NullPointerException("Delegate cannot be nnull");
        }
        this.delegate = delegate;
    }

    @Override
    protected List<Station> doInBackground(List<Station>... params) {
        Log.d(TAG, "Launch background update...");

        final List<Station> stations = new ArrayList<Station>(params[0]);

        for (Station station : stations) {
            if (isCancelled()) {
                Log.d(TAG, "Task has been cancelled.");
                return stations;
            }

            station = XML_READER.getRemoteInfo(station);
            delegate.update(station);

            publishProgress();
        }

        return stations;
    }

}

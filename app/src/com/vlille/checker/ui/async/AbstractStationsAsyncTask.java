package com.vlille.checker.ui.async;

import android.os.AsyncTask;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.ui.delegate.StationUpdateDelegate;
import com.vlille.checker.xml.XMLReader;

import java.util.List;

/**
 * An abstract AsyncTask to retrieve details from a stations list.
 */
public abstract class AbstractStationsAsyncTask extends AsyncTask<List<Station>, Void, List<Station>> {

	private static final String TAG = "AsyncStationTaskUpdater";
	
	private static final XMLReader XML_READER = new XMLReader();

    private StationUpdateDelegate delegate;

    @Override
	protected List<Station> doInBackground(List<Station>... params) {
		Log.d(TAG, "Launch background update...");
		
		final List<Station> stations = params[0];

        synchronized (stations) {
            for (Station station : stations) {
                if (isCancelled()) {
                    Log.d(TAG, "Task has been cancelled.");
                    break;
                }
                station = XML_READER.getRemoteInfo(station);
                if (delegate != null) {
                    delegate.update(station);
                }

                publishProgress();
            }
        }

		return stations;
	}

    public void setDelegate(StationUpdateDelegate delegate) {
        this.delegate = delegate;
    }

}

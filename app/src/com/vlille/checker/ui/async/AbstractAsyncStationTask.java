package com.vlille.checker.ui.async;

import android.os.AsyncTask;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.xml.XMLReader;

import java.util.List;

/**
 * An abstract AsyncTask to retrieve details from a stations list.
 */
public abstract class AbstractAsyncStationTask extends AsyncTask<List<Station>, Void, List<Station>> {

	private static final String TAG = "AsyncStationTaskUpdater";
	
	private final XMLReader XML_READER = new XMLReader();
	
	@Override
	protected List<Station> doInBackground(List<Station>... params) {
		Log.d(TAG, "Launch background update...");
		
		final List<Station> stations = params[0];
		
		for (Station eachStation : stations) {
            if (isCancelled()) {
                Log.d(TAG, "Task has been cancelled.");
                break;
            }

            Log.d(TAG, "Update details from " + eachStation.getName());

			XML_READER.updateDetails(eachStation);
            publishProgress();
		}
		
		return stations;
	}

}

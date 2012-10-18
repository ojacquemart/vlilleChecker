package com.vlille.checker.ui.async;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.vlille.checker.model.Station;
import com.vlille.checker.xml.XMLReader;

public abstract class AbstractAsyncStationTask extends AsyncTask<List<Station>, Void, List<Station>> {

	private final String TAG = getClass().getSimpleName();
	
	private final XMLReader XML_READER = new XMLReader();
	
	@Override
	protected List<Station> doInBackground(List<Station>... params) {
		Log.d(TAG, "doInBackground");
		final List<Station> stations = params[0];
		
		for (Station eachStation : stations) {
			final boolean upToDate = eachStation.isUpToDate();
			if (!upToDate) {
				XML_READER.updateDetails(eachStation);
			}
		}
		
		return stations;
	}

}

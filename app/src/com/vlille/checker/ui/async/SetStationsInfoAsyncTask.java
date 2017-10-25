package com.vlille.checker.ui.async;

import android.os.AsyncTask;

import com.vlille.checker.dataset.StationRepository;
import com.vlille.checker.model.SetStationsInfo;

/**
 * Retrieve all stations information from the local asset xml.
 */
public class SetStationsInfoAsyncTask extends AsyncTask<Void, Void, SetStationsInfo> {

    private SetStationsDelegate delegate;

    public SetStationsInfoAsyncTask(SetStationsDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected SetStationsInfo doInBackground(Void... params) {
        return StationRepository.getSetStationsInfo();
    }

    @Override
    protected void onPostExecute(SetStationsInfo setStationsInfo) {
        delegate.handleResult(setStationsInfo);

        super.onPostExecute(setStationsInfo);
    }

    public interface SetStationsDelegate {
        void handleResult(SetStationsInfo setStationsInfo);
    }
}
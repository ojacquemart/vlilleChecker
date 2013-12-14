package com.vlille.checker.ui.async;

import android.os.AsyncTask;

import com.vlille.checker.Application;
import com.vlille.checker.R;
import com.vlille.checker.db.DBUpdater;
import com.vlille.checker.utils.ToastUtils;

/**
 * An {@link AsyncTask} to refresh stations from vlille.fr.
 */
public class DBUpdaterAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private AsyncTaskResultListener asyncListener;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (asyncListener != null) {
            asyncListener.onAsyncTaskPreExecute();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return new DBUpdater().update();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if (asyncListener != null) {
            asyncListener.onAsyncTaskPostExecute(result);
        }

        int resourceId = result
                ?  R.string.data_status_update_done
                : R.string.data_status_uptodate;

        ToastUtils.show(Application.getContext(), resourceId);
    }

    public void setAsyncListener(AsyncTaskResultListener asyncListener) {
        this.asyncListener = asyncListener;
    }

}
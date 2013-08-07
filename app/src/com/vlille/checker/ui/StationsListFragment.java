package com.vlille.checker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.async.AbstractAsyncStationTask;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * A generic fragment to load and handle selectable stations.
 */
abstract class StationsListFragment extends SherlockListFragment
        implements AbsListView.OnScrollListener, PullToRefreshAttacher.OnRefreshListener {

    private static final String TAG = StationsListFragment.class.getName();

    /**
     * The pullToRefreshAttach.
     */
    private PullToRefreshAttacher pullToRefreshAttacher;

    /**
     * The current activity.
     */
    private Activity activity;

    /**
     * The stations list used by the adapter.
     */
    private List<Station> stations;

    /**
     * The ListView adapter.
     */
    private DefaultStationsAdapter adapter;

    /**
     * The current AsyncTask.
     */
    private StationsAsyncTask asyncTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        activity = getActivity();
        pullToRefreshAttacher = PullToRefreshAttacher.get(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        pullToRefreshAttacher.addRefreshableView(getListView(), this);

        loadStations();
        initListAdapter();
        setOnScrollListener();
    }

    abstract void loadStations();

    public void initListAdapter() {
        setListAdapter();
    }

    public void setListAdapter() {
        setListAdapter(getAdapter());
    }

    private DefaultStationsAdapter getAdapter() {
        adapter = new DefaultStationsAdapter(activity, R.layout.stars_list_content, stations);
        adapter.setReadOnly(isReadOnly());

        return adapter;
    }

    /**
     * Returns whether if the view allows to remove elements.
     *
     * @return <code>true</code> when elements can be removed from the list, <code>false</code> otherwise.
     */
    abstract boolean isReadOnly();

    private void setOnScrollListener() {
        getListView().setOnScrollListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        updateVisibleItemsAsRunnable();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        cancelAsyncTask();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        Log.d(TAG, "onScrollStateChanged with state " + scrollState);

        if (scrollState == SCROLL_STATE_IDLE) {
            updateVisibleItems();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onRefreshStarted(View view) {
        if (updateOnRefreshStarted()) {
            updateVisibleItems();
        }
    }

    /**
     * Returns whetever if visible elements need to be refresh on pull.
     */
    abstract boolean updateOnRefreshStarted();

    /**
     * Update visible stations using the ListView#post method to get the correct last visible item position.
     */
    public void updateVisibleItemsAsRunnable() {
        getListView().post(new Runnable() {
            @Override
            public void run() {
                updateVisibleItems();
            }
        });
    }

    /**
     * Update visible stations.
     */
    public void updateVisibleItems() {
        int lastVisibleRowPosition = getLastVisiblePosition();
        Log.d(TAG, "Index of last visible row = " + lastVisibleRowPosition);

        if (lastVisibleRowPosition > 0) {
            int firstVisibleRow = getListView().getFirstVisiblePosition();
            Log.d(TAG, String.format(
                "Update only visible stations from %d to %d for a list of %d elements",
                firstVisibleRow,
                lastVisibleRowPosition,
                stations.size())
            );

            List<Station> subStations = stations.subList(firstVisibleRow, lastVisibleRowPosition);

            asyncTask = getNewAsyncTask();
            asyncTask.execute(subStations);
        }
    }

    private int getLastVisiblePosition() {
        if (stations.isEmpty()) {
            return 0;
        }

        // +1 because of the -1 in ListView#getLastVisiblePosition()
        int lastVisibleRowPosition = getListView().getLastVisiblePosition() + 1;
        if (lastVisibleRowPosition > stations.size()) {
            return stations.size();
        }

        return lastVisibleRowPosition;
    }

    /**
     * Cancels the maybe running task and gets a new one.
     */
    private StationsAsyncTask getNewAsyncTask() {
        cancelAsyncTask();
        asyncTask = new StationsAsyncTask();

        return asyncTask;
    }

    /**
     * Cancels the maybe running async task.
     */
    private void cancelAsyncTask() {
        if (asyncTask != null) {
            asyncTask.cancel();
        }
    }

    public void setStations(List<Station> stations) {
        Log.d(TAG, String.format("Set %d stations", stations.size()));

        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations;
    }

    /**
     * An AsyncTask to load details from the #getStations method.
     */
    class StationsAsyncTask extends AbstractAsyncStationTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            setProgressIndeterminateVisibility(true);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "Progress update...");
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(List<Station> result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute");

            setProgressIndeterminateVisibility(false);
        }

        public void cancel() {
            if (getStatus() == Status.RUNNING) {
                Log.d(TAG, "Cancel the async task");
                cancel(false);
                setProgressIndeterminateVisibility(false);
            }
        }

        private void setProgressIndeterminateVisibility(boolean visible) {
            pullToRefreshAttacher.setRefreshing(visible);
        }
    }

}

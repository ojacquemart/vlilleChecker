package com.vlille.checker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.vlille.checker.R;
import com.vlille.checker.db.StationEntityManager;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.async.AbstractStationsAsyncTask;
import com.vlille.checker.utils.ContextHelper;

import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.fragment.sherlock.ListFragment;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * A generic fragment to load and handle selectable stations.
 */
abstract class StationsListFragment extends ListFragment
        implements AbsListView.OnScrollListener, PullToRefreshAttacher.OnRefreshListener, StationUpdateDelegate {

    private static final String TAG = StationsListFragment.class.getName();

    @InjectDependency
    protected StationEntityManager stationEntityManager;

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
    private StationsAdapter adapter;

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
        initListViewListeners();
    }

    abstract void loadStations();

    public void initListAdapter() {
        setListAdapter();
    }

    public void setListAdapter() {
        setListAdapter(getAdapter());
    }

    private StationsAdapter getAdapter() {
        adapter = new StationsAdapter(activity, R.layout.station_list_item, stations);
        adapter.setReadOnly(isReadOnly());
        adapter.setStationUpdateDelegate(this);

        return adapter;
    }

    /**
     * Returns whether if the view allows to remove elements.
     *
     * @return <code>true</code> when elements can be removed from the list, <code>false</code> otherwise.
     */
    abstract boolean isReadOnly();

    private void initListViewListeners() {
        getListView().setOnScrollListener(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Log.d(TAG, "Item clicked = " + position + " " + id);
                if (id < stations.size()) {
                    // Unselect all stations.
                    for (Station station : stations) {
                        station.setSelected(false);
                    }

                    // Select the station and notify the adapter.
                    Station clickedStation = stations.get((int) id);
                    clickedStation.setSelected(true);
                    Log.d(TAG, "Station clicked = " + clickedStation.getName());

                    StationsListFragment.this.adapter.notifyDataSetChanged();
                }
            }
        });
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

        int firstVisiblePosition = absListView.getFirstVisiblePosition();
        if (scrollState == SCROLL_STATE_IDLE && firstVisiblePosition > 0) {
            updateVisibleItems();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onRefreshStarted(View view) {
        updateVisibleItems();
    }

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
        if (!ContextHelper.isNetworkAvailable(activity)) {
            setProgressIndeterminateVisibility(false);
        } else {
            doUpdateVisibleItems();
        }
    }

    private void doUpdateVisibleItems() {
        int lastVisibleRowPosition = getLastVisiblePosition();
        Log.d(TAG, "Index of last visible row = " + lastVisibleRowPosition);

        if (lastVisibleRowPosition > 0) {
            int firstVisiblePosition = getFirstVisiblePosition();
            Log.d(TAG, String.format(
                "Update only visible stations from %d to %d for a list of %d elements",
                    firstVisiblePosition,
                lastVisibleRowPosition,
                stations.size())
            );

            List<Station> subStations = stations.subList(firstVisiblePosition, lastVisibleRowPosition);

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

    private int getFirstVisiblePosition() {
        int firstVisibleRow = getListView().getFirstVisiblePosition();
        if (firstVisibleRow > 1 && stations.size() > 1) {
            // -1 to load the almost visible row above the first visible.
            return firstVisibleRow - 1;
        }

        return firstVisibleRow;
    }

    /**
     * Cancels the maybe running task and gets a new one.
     */
    private StationsAsyncTask getNewAsyncTask() {
        cancelAsyncTask();
        asyncTask = new StationsAsyncTask();
        asyncTask.setDelegate(this);

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

    private void setProgressIndeterminateVisibility(boolean visible) {
        pullToRefreshAttacher.setRefreshing(visible);
    }

    public void setStations(List<Station> stations) {
        Log.d(TAG, String.format("Set %d stations", stations.size()));

        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public void update(Station station) {
        stationEntityManager.update(station);
    }

    /**
     * An AsyncTask to load details from the #getStations method.
     */
    class StationsAsyncTask extends AbstractStationsAsyncTask {

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

    }

}

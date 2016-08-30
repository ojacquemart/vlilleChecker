package com.vlille.checker.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.vlille.checker.R;
import com.vlille.checker.db.StationEntityManager;
import com.vlille.checker.model.Station;
import com.vlille.checker.model.StationHolder;
import com.vlille.checker.ui.HomeActivity;
import com.vlille.checker.ui.IntentCommunication;
import com.vlille.checker.ui.StationInfoActivity;
import com.vlille.checker.ui.async.AbstractStationsAsyncTask;
import com.vlille.checker.ui.delegate.StationUpdateDelegate;
import com.vlille.checker.ui.listener.MapTabListener;
import com.vlille.checker.ui.fragment.adapter.StationsAdapter;
import com.vlille.checker.utils.ContextHelper;

import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.fragment.support.v4.ListFragment;

import java.util.List;

/**
 * A generic fragment to load and handle selectable stations.
 */
abstract class StationsListFragment extends ListFragment
        implements AbsListView.OnScrollListener,
            SwipeRefreshLayout.OnRefreshListener,
            StationUpdateDelegate {

    private static final String TAG = StationsListFragment.class.getName();

    private static final int[] SWIPE_COLORS = {
            R.color.primary,
            R.color.yellow,
            R.color.activated };

    @InjectDependency
    protected StationEntityManager stationEntityManager;

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

    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(Bundle savedInstanceState,
                             LayoutInflater inflater, ViewGroup container) {
        Log.d(TAG, "onCreateView");
        inflater.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(getSwipeableResource(), container, false);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(SWIPE_COLORS);

        return view;
    }

    protected abstract int getSwipeableResource();

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        setListAdapter(null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        loadStations();
        initListAdapter();
        initListViewListeners();
    }

    @Override
    public void onRefresh() {
        updateVisibleItems();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    public HomeActivity getHomeActivity() {
        return (HomeActivity) getActivity();
    }

    abstract void loadStations();

    public void initListAdapter() {
        setListAdapter();
    }

    public void setListAdapter() {
        setListAdapter(getAdapter());
    }

    private StationsAdapter getAdapter() {
        adapter = new StationsAdapter(getActivity(), R.layout.station_list_item, stations);
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
            public void onItemClick(AdapterView<?> adapter, View view, int position, long index) {
                Log.d(TAG, "Item clicked = " + position + " " + index);
                if (index < stations.size()) {
                    // Select the station and notify the adapter.
                    Station clickedStation = stations.get((int) index);
                    Log.d(TAG, "Station clicked = " + clickedStation.getName());

                    Intent intent = new Intent(getHomeActivity(), StationInfoActivity.class);
                    intent.putExtra(IntentCommunication.STATION_DATA, new StationHolder(clickedStation, (int) index));

                    startActivityForResult(intent, IntentCommunication.STATION_INFO_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (IntentCommunication.Result.shouldMoveMapToStation(resultCode)) {
            selectMapGeoPoint(data);
        } else if (IntentCommunication.Result.shouldStarStation(resultCode)) {
            cancelAsyncTask();

            final StationHolder stationHolder = (StationHolder) data.getExtras().get(IntentCommunication.STATION_DATA);
            final Station station = stationHolder.getStation();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Change station data");
                    adapter.changeStation(station.isStarred(), stationHolder.getIndex());
                }
            });
        }
    }

    private void selectMapGeoPoint(Intent data) {
        HomeActivity homeActivity = getHomeActivity();
        StationHolder holder = (StationHolder) data.getExtras().get(IntentCommunication.STATION_DATA);

        MapTabListener mapTabListener = new MapTabListener(homeActivity, holder.getStation().getGeoPoint());

        ActionBar.Tab mapTab = homeActivity.getSupportActionBar().getTabAt(2);
        mapTab.setTabListener(mapTabListener);
        mapTab.select();
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
        if (!ContextHelper.isNetworkAvailable(getActivity())) {
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
        asyncTask = new StationsAsyncTask(this);

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
        swipeLayout.setRefreshing(visible);
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

        StationsAsyncTask(StationUpdateDelegate delegate) {
            super(getHomeActivity(), delegate);
        }

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

package com.vlille.checker.ui;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.async.AbstractAsyncStationTask;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.ViewUtils;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Fragment activity wich displays starred stations.
 */
public class StarsListFragment extends SherlockListFragment
        implements PullToRefreshAttacher.OnRefreshListener {

    private final String TAG = getClass().getSimpleName();

    private FragmentActivity activity;
    private PullToRefreshAttacher pullToRefreshAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        activity = getActivity();

        pullToRefreshAttacher = PullToRefreshAttacher.get(activity);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        inflater.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.stars_list_layout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        pullToRefreshAttacher.setRefreshing(true);
        setStarsAdapter();
    }


    private void setStarsAdapter() {
        final List<Station> starredStations = VlilleChecker.getDbAdapter().getStarredStations();
        boolean isEmptyStarredStations = starredStations.isEmpty();
        Log.d(TAG, "Starred stations empty? " + isEmptyStarredStations);

        ViewUtils.switchView(activity.findViewById(R.id.home_nostations_nfo), isEmptyStarredStations);
        if (isEmptyStarredStations) {
            setRefreshComplete();
        } else {
            loadDetails(starredStations);
        }
    }

    private void setRefreshComplete() {
        pullToRefreshAttacher.setRefreshComplete();
    }

    private void loadDetails(List<Station> stations) {
        Log.d(TAG, "loadDetails");
        // Just to display some toast if network is not up.
        ContextHelper.isNetworkAvailable(activity);

        try {
            new AsyncListStationReader().execute(stations);
        } catch (Exception e) {
            Log.e(TAG, "handleStarredStations", e);
        }
    }

    /**
     * Handle adapter.
     *
     * @param stations the stations to put into the adapter.
     * @return <code>false</code> if the activity is null for some reason, <code>false</code> otherwise.
     */
    private boolean handleAdapter(final List<Station> stations) {
        if (activity == null) {
            return false;
        }

        final StarsListAdapter adapter = new StarsListAdapter(
                activity,
                R.layout.stars_list_content, stations);

        pullToRefreshAttacher.addRefreshableView(getListView(), this);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public void onRefreshStarted(View view) {
        Log.d(TAG, "onRefreshStarted");

        setStarsAdapter();
    }

    class AsyncListStationReader extends AbstractAsyncStationTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "onPreExecute");
        }

        @Override
        protected void onPostExecute(List<Station> result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute");

            if (!handleAdapter(result)) {
                Toast.makeText(activity, R.string.error_connection_expired, Toast.LENGTH_SHORT).show();
            }

            setRefreshComplete();
        }

    }

}

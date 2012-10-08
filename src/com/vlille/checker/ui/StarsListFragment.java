package com.vlille.checker.ui;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.async.AbstractAsyncStationTask;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.ToastUtils;

/**
 * Fragment activity wich displays starred activities.
 */
public class StarsListFragment extends VlilleSherlockListFragment {

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setListShown(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		setStarsAdapter();
	}

	private void setStarsAdapter() {
		final List<Station> starredStations = VlilleChecker.getDbAdapter()
				.getStarredStations();

		boolean isEmptyStarredStations = starredStations.isEmpty();
		Log.d(TAG, "Starred stations empty? " + isEmptyStarredStations);
		if (isEmptyStarredStations) {
			showNoStationsNfo();
		} else {
			loadDetails(starredStations);
		}
	}

	private void loadDetails(List<Station> stations) {
		Log.d(TAG, "loadDetails");
		if (!ContextHelper.isNetworkAvailable(activity)) {
			ToastUtils.show(activity, R.string.error_no_connection);
		}
		
		boolean error = false;
		
		try {
			new AsyncListStationReader().execute(stations);
		} catch (Exception e) {
			Log.e(TAG, "handleStarredStations", e);
			error = true;
		}
		
		if (error) {
			ToastUtils.show(activity, R.string.error_connection_expired);
		}
	}
	
	private void handleAdapter(final List<Station> stations) {
		final StarsListAdapter adapter = new StarsListAdapter(activity,
				R.layout.stars_list_content, stations, null);
		setListAdapter(adapter);
	}
	
	class AsyncListStationReader extends AbstractAsyncStationTask {
		
		@Override
		protected void onPostExecute(List<Station> result) {
			super.onPostExecute(result);
			Log.d(TAG, "onPostExecute");
			
			handleAdapter(result);
			setListShownNoAnimation(true);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d(TAG, "onPreExecute");
			
			setListShownNoAnimation(false);
		}
		
	}

}

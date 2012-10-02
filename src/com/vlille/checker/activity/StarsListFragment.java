package com.vlille.checker.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.service.AbstractRetrieverService;
import com.vlille.checker.service.StationsResultReceiver;
import com.vlille.checker.service.StationsResultReceiver.Receiver;
import com.vlille.checker.service.StationsRetrieverService;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.ToastUtils;

/**
 * Fragment activity wich displays starred activities.
 */
public class StarsListFragment extends VlilleSherlockListFragment implements Receiver {
	
	private StationsResultReceiver resultReceiver;

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
		final List<Station> starredStations = VlilleChecker.getDbAdapter().getStarredStations();
		
		boolean isEmptyStarredStations = starredStations.isEmpty();
		Log.d(TAG, "Starred stations empty? " + isEmptyStarredStations);
		if (isEmptyStarredStations) {
			showNoStationsNfo();
		} else {
			handleStarredStations(starredStations);
		}
	}

	private void handleStarredStations(List<Station> starredIdsStations) {
		if (!ContextHelper.isNetworkAvailable(activity)) {
			ToastUtils.show(activity, R.string.error_no_connection);
		}
		
		if (!activity.isFinishing()) {
			 setListShownNoAnimation(false);
		}

		Log.d(TAG, "Start retriever service.");
		resultReceiver = new StationsResultReceiver(new Handler());
		resultReceiver.setReceiver(this);

		final Intent intent = new Intent(Intent.ACTION_SYNC, null, activity,
				StationsRetrieverService.class);
		intent.putExtra(RECEIVER, resultReceiver);
		intent.putExtra(AbstractRetrieverService.EXTRA_DATA, (ArrayList<Station>) starredIdsStations);
		activity.startService(intent);
	}

	/**
	 * TODO: add loader.
	 */
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		boolean finished = false;
		boolean error = false;

		switch (resultCode) {
		case Receiver.RUNNING:
			Log.d(TAG, "Retrieve in progress");

			break;
		case Receiver.FINISHED:
			Log.d(TAG, "All starred stations loaded");
			finished = true;

			@SuppressWarnings("unchecked")
			List<Station> results = (List<Station>) resultData.getSerializable(AbstractRetrieverService.RESULTS);
			handleAdapter(results);

			break;
		case Receiver.ERROR:
			finished = true;
			error = true;

			break;
		}

		if (finished && !activity.isFinishing()) {
			setListShownNoAnimation(true);
		}

		if (error) {
			ToastUtils.show(activity, R.string.error_connection_expired);
		}
	}

	/**
	 * Handle adapter listview
	 * 
	 * @param stations
	 *            The starred stations details.
	 */
	private void handleAdapter(final List<Station> stations) {
		final StarsListAdapter adapter = new StarsListAdapter(activity, R.layout.stars_list_content, stations, null);
		setListAdapter(adapter);
	}
	
}
